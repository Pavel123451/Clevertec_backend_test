package ru.clevertec.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.config.DataSourceConfig;
import ru.clevertec.dao.ConnectionPoolManager;
import ru.clevertec.dao.impl.ProductDao;
import ru.clevertec.models.Product;
import ru.clevertec.utils.JsonUtil;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private ProductDao productDao;
    private ConnectionPoolManager connectionPoolManager;

    @Override
    public void init() throws ServletException {
        try {
            DataSource dataSource = DataSourceConfig.getDataSource();
            connectionPoolManager = new ConnectionPoolManager(dataSource, 10);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String idParam = req.getParameter("id");

        try (Connection connection = connectionPoolManager.getConnection();
             PrintWriter writer = resp.getWriter()) {
            productDao = new ProductDao(connection);
            resp.setContentType("application/json");

            if (idParam == null) {
                List<Product> products = productDao.getAll();
                writer.write(JsonUtil.toJson(products));
            } else {
                int id = Integer.parseInt(idParam);
                Product product = productDao.getById(id);
                if (product != null) {
                    writer.write(JsonUtil.toJson(product));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try (Connection connection = connectionPoolManager.getConnection();
             PrintWriter writer = resp.getWriter()) {
            productDao = new ProductDao(connection);
            resp.setContentType("application/json");

            Product product = JsonUtil.fromJson(req.getReader(), Product.class);
            productDao.save(product);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            writer.write(JsonUtil.toJson(product));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String idParam = req.getParameter("id");

        try (Connection connection = connectionPoolManager.getConnection();
             PrintWriter writer = resp.getWriter()) {
            productDao = new ProductDao(connection);
            resp.setContentType("application/json");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Long id = Long.parseLong(idParam);
            Product product = JsonUtil.fromJson(req.getReader(), Product.class);
            product.setId(id);
            productDao.update(product);
            writer.write(JsonUtil.toJson(product));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String idParam = req.getParameter("id");

        try (Connection connection = connectionPoolManager.getConnection()){
            productDao = new ProductDao(connection);
            resp.setContentType("application/json");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idParam);
            productDao.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void destroy() {
        connectionPoolManager.closeAllConnections();
    }
}


