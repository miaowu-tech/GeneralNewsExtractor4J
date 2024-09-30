package tech.miaowu.spider.extractor.news;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 *
 * @author anselwang
 * @since v0.1.0
 */
public class EncryptUtils {
    /**
     * MD5，信息摘要算法（Message-Digest Algorithm5），一种被广泛使用的密码散列函数，可以产生出一个固定长度的散列值。用于确保信息传输完整一致。
     * <p>
     * 输入：待加密的字符串
     * 输出：128位（16字节）或32个16进制字符（常用）
     * 应用：密码管理、数字签名、文件完整性校验
     * 安全性：★☆☆☆☆
     *
     * @param plainString 明文
     * @return cipherString 密文
     */
    public static String md5(String plainString) {
        String cipherString = null;
        try {
            // 获取实例
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 计算摘要
            byte[] cipherBytes = messageDigest.digest(plainString.getBytes(StandardCharsets.UTF_8));
            // 输出为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : cipherBytes) {
                sb.append(String.format("%02x", b));
            }
            cipherString = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cipherString;
    }
}
