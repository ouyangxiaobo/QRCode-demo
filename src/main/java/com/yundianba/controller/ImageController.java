package com.yundianba.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yundianba.util.LogoConfig;
import com.yundianba.util.MatrixToImageWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.yundianba.util.MatrixToImageWriter.addLogo_QRCode;
import static com.yundianba.util.MatrixToImageWriter.pressText;

/**
 * @author：ouyang
 * @Date: 2018/8/16
 * @desc:
 */

@Controller
public class ImageController {

    //二维码内容
    private static String content = "http://www.baidu.com";

    //logo存放的地方
    private static String path = "C:/Users/ouyang/Desktop/二维码";

    @RequestMapping(value = "imageWithLogo")
    public Boolean image(HttpServletResponse response, Model model) throws  Exception {


            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            @SuppressWarnings("rawtypes")
            Map hints = new HashMap();

            //设置UTF-8， 防止中文乱码
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //设置二维码四周白色区域的大小
            hints.put(EncodeHintType.MARGIN, 2);
            //设置二维码的容错性
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            //width:图片完整的宽;height:图片完整的高
            //因为要在二维码下方附上文字，所以把图片设置为长方形（高大于宽）
            int width = 352;//352
            int height = 500;//612

            //画二维码，记得调用multiFormatWriter.encode()时最后要带上hints参数，不然上面设置无效
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            //qrcFile用来存放生成的二维码图片（无logo，无文字）
            File logoFile = new File(path, "logo.jpg");

            //开始画二维码
            BufferedImage barCodeImage = MatrixToImageWriter.writeToFile(bitMatrix, "jpg");

            //在二维码中加入图片
            LogoConfig logoConfig = new LogoConfig(); //LogoConfig中设置Logo的属性
            BufferedImage image = addLogo_QRCode(barCodeImage, logoFile, logoConfig);

            int font = 30; //字体大小
            int fontStyle = 1; //字体风格

            //用来存放带有logo+文字的二维码图片
            String newImageWithText = path + "/imageWithText.jpg";
            //附加在图片上的文字信息
            String text = "云 电 吧";

            //在二维码下方添加文字（文字居中）
            pressText(text, newImageWithText, image, fontStyle, Color.black, font, width, height);




            OutputStream stream = response.getOutputStream();
            //不要缓存
            response.setDateHeader("expires", -1);
            //告诉所有浏览器不要缓存


            response.setHeader("Cache-control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("content-type", "image/jpg");



            return ImageIO.write(image,"jpg",stream);








    }

}
