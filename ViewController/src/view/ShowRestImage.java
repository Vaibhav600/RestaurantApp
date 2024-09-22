package view;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "ShowRestImage", urlPatterns = { "/showrestimage" })
public class ShowRestImage extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ImageName = request.getParameter("imgName");
        OutputStream output = response.getOutputStream();
        String ImagePath = "/Users/Shared/"+ImageName+".png";
        File ImageFile = new File(ImagePath);
        BufferedImage inputImg = ImageIO.read(ImageFile);
        ImageIO.write(inputImg,"PNG",output);
        output.flush();
        output.close();
    }
}
