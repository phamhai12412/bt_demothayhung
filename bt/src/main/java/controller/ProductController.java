package controller;

import model.Product;
import service.ProductService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@WebServlet(name = "ProductController", value = "/ProductController")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class ProductController extends HttpServlet {
    protected ProductService productService;

    @Override
    public void init() throws ServletException {
        productService= new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action==null){
            displayProducts(request,response);
        }else {
            switch (action){
                case "CREATE":
                    request.getRequestDispatcher("/WEB-INF/newProduct.jsp").forward(request,response);
                    break;
                case "DETAIL":
                    Long id = Long.valueOf(request.getParameter("id"));
                    Product p = productService.findById(id);
                    request.setAttribute("product",p);
                    request.getRequestDispatcher("/WEB-INF/productDetail.jsp").forward(request,response);
            }
        }
    }

    protected void displayProducts(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        request.setCharacterEncoding("UTF-8");
        List<Product> products = productService.findAll();
        request.setAttribute("products",products);
        request.getRequestDispatcher("/WEB-INF/listProducts.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        System.out.println(action);
        if(action!=null){
            switch (action){
                case "ADD":
                    String name = request.getParameter("name");
                    String des = request.getParameter("des");
                    Double price = Double.valueOf(request.getParameter("price"));
                    int stock = Integer.parseInt(request.getParameter("stock"));
                    Collection<Part> listImageUrl = request.getParts();

                    String avatar = "";
                    List<String> listImageUrls =  new ArrayList<>();
                    Date date =new Date();
                    String imagePath = "C:\\Users\\PHAM HAI\\Desktop\\bt\\src\\main\\webapp\\image";
                    for (Part part:listImageUrl) {
                        // lấy avatar
                        if(part.getName().equals("image")){
                            //  ghi 1 ảnh vào thư mục chỉ định
                            part.write(imagePath+ File.separator+part.getSubmittedFileName());
                            avatar = part.getSubmittedFileName();
                        }else if(part.getName().equals("imageUrls")){
                            // xử lí nhiều file
                            part.write(imagePath+ File.separator+part.getSubmittedFileName());
                            listImageUrls.add(part.getSubmittedFileName());
                        }
                    }

                    Product newProduct= new Product(null,name,des,listImageUrls,price,stock,avatar);
                    productService.save(newProduct);
                    break;
            }
            response.sendRedirect("/");
        }
    }
}