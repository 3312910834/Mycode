package com.campus.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebServlet("/api/upload")
// 开启文件上传支持，限制单文件最大10MB
@MultipartConfig(maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 20)
public class UploadApiServlet extends HttpServlet {

    // 允许的图片格式白名单，保证健壮性
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".webp", ".gif", ".bmp"
    );

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();

        try {
            Part part = req.getPart("file");
            if (part == null || part.getSize() == 0) {
                out.println("{\"code\": 400, \"message\": \"未检测到文件\"}");
                return;
            }

            // 1. 校验 MIME 类型，防止恶意文件伪装
            String contentType = part.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
                out.println("{\"code\": 400, \"message\": \"文件类型不合法，仅支持图片\"}");
                return;
            }

            // 2. 获取并校验后缀名
            String submittedFileName = part.getSubmittedFileName();
            String extension = "";
            if (submittedFileName != null && submittedFileName.lastIndexOf(".") != -1) {
                extension = submittedFileName.substring(submittedFileName.lastIndexOf(".")).toLowerCase();
            }

            if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension)) {
                out.println("{\"code\": 400, \"message\": \"不支持的格式，允许：jpg/png/webp/gif/bmp\"}");
                return;
            }

            // 3. 生成唯一文件名
            String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;

            // 4. 智能判断操作系统：Windows 存 D 盘，Linux 存 /www/uploads
            String os = System.getProperty("os.name").toLowerCase();
            String saveDirPath = os.contains("win") ? "D:/upload" : "/www/uploads";

            File saveDir = new File(saveDirPath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // 5. 保存文件到物理硬盘
            String absolutePath = saveDirPath + File.separator + newFileName;
            part.write(absolutePath);

            // 6. 返回网络可访问的虚拟路径给前端
            String fileUrl = "/uploads/" + newFileName;
            out.println("{\"code\": 200, \"message\": \"上传成功\", \"data\": \"" + fileUrl + "\"}");

        } catch (IllegalStateException e) {
            out.println("{\"code\": 400, \"message\": \"文件过大，请上传10MB以内的图片\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"code\": 500, \"message\": \"服务器内部异常: " + e.getMessage() + "\"}");
        }
    }
}