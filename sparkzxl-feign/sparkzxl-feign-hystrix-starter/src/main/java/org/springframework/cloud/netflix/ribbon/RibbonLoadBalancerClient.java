package org.springframework.cloud.netflix.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.springframework.cloud.netflix.ribbon.RibbonUtils.updateToSecureConnectionIfNeeded;

/**
 * description: RibbonLoadBalancerClient 重写
 * 兼容spring cloud 2020 新版本
 *
 * @author zhouxinlei
 * @since 2022-07-11 14:07:44
 */
public class RibbonLoadBalancerClient implements LoadBalancerClient {

    private final SpringClientFactory clientFactory;

    public RibbonLoadBalancerClient(SpringClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public URI reconstructURI(ServiceInstance instance, URI original) {
        Assert.notNull(instance, "instance can not be null");
        String serviceId = instance.getServiceId();
        RibbonLoadBalancerContext context = this.clientFactory
                .getLoadBalancerContext(serviceId);

        URI uri;
        Server server;
        if (instance instanceof RibbonServer) {
            RibbonServer ribbonServer = (RibbonServer) instance;
            server = ribbonServer.getServer();
            uri = updateToSecureConnectionIfNeeded(original, ribbonServer);
        } else {
            server = new Server(instance.getScheme(), instance.getHost(),
                    instance.getPort());
            IClientConfig clientConfig = clientFactory.getClientConfig(serviceId);
            ServerIntrospector serverIntrospector = serverIntrospector(serviceId);
            uri = updateToSecureConnectionIfNeeded(original, clientConfig,
                    serverIntrospector, server);
        }
        return context.reconstructURIWithServer(server, uri);
    }

    @Override
    public ServiceInstance choose(String serviceId) {
        return choose(serviceId, null);
    }

    @Override
    public <T> ServiceInstance choose(String serviceId, Request<T> request) {
        Server server = getServer(getLoadBalancer(serviceId), request);
        if (server == null) {
            return null;
        }
        return new RibbonServer(serviceId, server, isSecure(server, serviceId),
                serverIntrospector(serviceId).getMetadata(server));
    }

    /**
     * New: Select a server using a 'key'.
     *
     * @param serviceId of the service to choose an instance for
     * @param hint      to specify the service instance
     * @return the selected {@link ServiceInstance}
     */
    public ServiceInstance choose(String serviceId, Object hint) {
        Server server = getServer(getLoadBalancer(serviceId), hint);
        if (server == null) {
            return null;
        }
        return new RibbonServer(serviceId, server, isSecure(server, serviceId),
                serverIntrospector(serviceId).getMetadata(server));
    }

    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request)
            throws IOException {
        return execute(serviceId, request, null);
    }

    /**
     * New: Execute a request by selecting server using a 'key'. The hint will have to be
     * the last parameter to not mess with the `execute(serviceId, ServiceInstance,
     * request)` method. This somewhat breaks the fluent coding style when using a lambda
     * to define the LoadBalancerRequest.
     *
     * @param <T>       returned request execution result type
     * @param serviceId id of the service to execute the request to
     * @param request   to be executed
     * @param hint      used to choose appropriate {@link Server} instance
     * @return request execution result
     * @throws IOException executing the request may result in an {@link IOException}
     */
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request, Object hint)
            throws IOException {
        ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
        Server server = getServer(loadBalancer, hint);
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }
        RibbonServer ribbonServer = new RibbonServer(serviceId, server,
                isSecure(server, serviceId),
                serverIntrospector(serviceId).getMetadata(server));

        return execute(serviceId, ribbonServer, request);
    }

    @Override
    public <T> T execute(String serviceId, ServiceInstance serviceInstance,
                         LoadBalancerRequest<T> request) throws IOException {
        Server server = null;
        if (serviceInstance instanceof RibbonServer) {
            server = ((RibbonServer) serviceInstance).getServer();
        }
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }

        RibbonLoadBalancerContext context = this.clientFactory
                .getLoadBalancerContext(serviceId);
        RibbonStatsRecorder statsRecorder = new RibbonStatsRecorder(context, server);

        try {
            T returnVal = request.apply(serviceInstance);
            statsRecorder.recordStats(returnVal);
            return returnVal;
        }
        // catch IOException and rethrow so RestTemplate behaves correctly
        catch (IOException ex) {
            statsRecorder.recordStats(ex);
            throw ex;
        } catch (Exception ex) {
            statsRecorder.recordStats(ex);
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }

    private ServerIntrospector serverIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = this.clientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }

    private boolean isSecure(Server server, String serviceId) {
        IClientConfig config = this.clientFactory.getClientConfig(serviceId);
        ServerIntrospector serverIntrospector = serverIntrospector(serviceId);
        return RibbonUtils.isSecure(config, serverIntrospector, server);
    }

    // Note: This method could be removed?
    protected Server getServer(String serviceId) {
        return getServer(getLoadBalancer(serviceId), null);
    }

    protected Server getServer(ILoadBalancer loadBalancer) {
        return getServer(loadBalancer, null);
    }

    protected Server getServer(ILoadBalancer loadBalancer, Object hint) {
        if (loadBalancer == null) {
            return null;
        }
        // Use 'default' on a null hint, or just pass it on?
        return loadBalancer.chooseServer(hint != null ? hint : "default");
    }

    protected ILoadBalancer getLoadBalancer(String serviceId) {
        return this.clientFactory.getLoadBalancer(serviceId);
    }

    /**
     * Ribbon-server-specific {@link ServiceInstance} implementation.
     */
    public static class RibbonServer implements ServiceInstance {

        private final String serviceId;

        private final Server server;

        private final boolean secure;

        private final Map<String, String> metadata;

        public RibbonServer(String serviceId, Server server) {
            this(serviceId, server, false, Collections.emptyMap());
        }

        public RibbonServer(String serviceId, Server server, boolean secure,
                            Map<String, String> metadata) {
            this.serviceId = serviceId;
            this.server = server;
            this.secure = secure;
            this.metadata = metadata;
        }

        @Override
        public String getInstanceId() {
            return this.server.getId();
        }

        @Override
        public String getServiceId() {
            return this.serviceId;
        }

        @Override
        public String getHost() {
            return this.server.getHost();
        }

        @Override
        public int getPort() {
            return this.server.getPort();
        }

        @Override
        public boolean isSecure() {
            return this.secure;
        }

        @Override
        public URI getUri() {
            return DefaultServiceInstance.getUri(this);
        }

        @Override
        public Map<String, String> getMetadata() {
            return this.metadata;
        }

        public Server getServer() {
            return this.server;
        }

        @Override
        public String getScheme() {
            return this.server.getScheme();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RibbonServer{");
            sb.append("serviceId='").append(serviceId).append('\'');
            sb.append(", server=").append(server);
            sb.append(", secure=").append(secure);
            sb.append(", metadata=").append(metadata);
            sb.append('}');
            return sb.toString();
        }

    }

}