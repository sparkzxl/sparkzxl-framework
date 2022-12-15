package com.github.sparkzxl.core.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.entity.core.AuthUserInfo;
import com.google.common.collect.Lists;

import javax.crypto.IllegalBlockSizeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * description: sm4 test
 *
 * @author zhouxinlei
 * @since 2022-11-22 16:29:29
 */
public class Sm4UtilTest {

    public static void main(String[] args) throws InterruptedException {
        String key = RandomUtil.randomString(16);
        String iv = RandomUtil.randomString(16);
        System.out.println("SM4加密key： " + key);
        System.out.println("SM4加密iv： " + iv);
        List<String> algorithm = new ArrayList<>();
        //algorithm.add(("SM4/ECB/PKCS5PADDING"));
        //algorithm.add(("SM4/ECB/ISO10126PADDING"));
        //algorithm.add(("SM4/CBC/PKCS5PADDING"));
        //algorithm.add(("SM4/CBC/ISO10126PADDING"));
        //algorithm.add(("SM4/CTR/NOPADDING"));
        algorithm.add(("SM4/CTR/PKCS5PADDING"));
        //algorithm.add(("SM4/CTR/ISO10126PADDING"));
        //algorithm.add(("SM4/CTS/PKCS5PADDING"));
        //algorithm.add(("SM4/CTS/ISO10126PADDING"));
        //algorithm.add(("SM4/ECB/NOPADDING"));
        //algorithm.add(("SM4/CTS/NOPADDING"));
        //algorithm.add(("SM4/CBC/NOPADDING"));
        //algorithm.add(("SM4/PCBC/NOPADDING"));
        //algorithm.add(("SM4/PCBC/PKCS5PADDING"));
        //algorithm.add(("SM4/PCBC/ISO10126PADDING"));
        for (String s : algorithm) {
            //SM4加密
            try {
                System.out.println("SM4加密算法： " + s);

                List<AuthUserInfo> authUserInfos = Lists.newArrayList();
                for (int i = 0; i < 10; i++) {
                    AuthUserInfo<String> authUserInfo = new AuthUserInfo<>();
                    authUserInfo.setId(IdUtil.getSnowflake().nextIdStr());
                    authUserInfo.setName(RandomValueUtil.getChineseName());
                    authUserInfo.setAccount(RandomUtil.randomString(5));
                    authUserInfo.setStatus(Boolean.TRUE);
                    authUserInfo.setRoleList(Lists.newArrayList("admin"));
                    authUserInfos.add(authUserInfo);
                }
                String json = JsonUtil.toJson(authUserInfos);
                System.out.println("SM4加密原始数据： " + json);
                String encrypt = Sm4Util.encryptToBase64String(s, key.getBytes(), iv.getBytes(), json.getBytes());
                System.out.println("SM4加密数据密文： " + encrypt);

                //SM4解密
                byte[] decrypt = Sm4Util.decryptFromBase64String(s, key.getBytes(), iv.getBytes(), encrypt);
                System.out.println("SM4解密数据： " + new String(decrypt));
            } catch (Exception e) {
                if (e instanceof IllegalBlockSizeException) {
                    System.err.println("SM4解密数据：算法 " + s + "数据需自己手工对齐");
                } else {
                    System.err.println("SM4解密数据：算法 " + s + "::" + e.getMessage());
                }
            } finally {
                System.err.println("---------------------------------------");
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }


}