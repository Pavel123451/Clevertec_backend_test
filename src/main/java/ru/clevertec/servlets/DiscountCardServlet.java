package ru.clevertec.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.config.DataSourceConfig;
import ru.clevertec.dao.ConnectionPoolManager;
import ru.clevertec.dao.impl.DiscountCardDao;
import ru.clevertec.models.DiscountCard;
import ru.clevertec.utils.JsonUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/discountcards")
public class DiscountCardServlet extends HttpServlet {
    private DiscountCardDao discountCardDao;
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
            discountCardDao = new DiscountCardDao(connection);
            resp.setContentType("application/json");

            if (idParam == null) {
                List<DiscountCard> discountCards = discountCardDao.getAll();
                writer.write(JsonUtil.toJson(discountCards));
            } else {
                int id = Integer.parseInt(idParam);
                DiscountCard discountCard = discountCardDao.getById(id);
                if (discountCard != null) {
                    writer.write(JsonUtil.toJson(discountCard));
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
            discountCardDao = new DiscountCardDao(connection);
            resp.setContentType("application/json");

            DiscountCard discountCard = JsonUtil.fromJson(req.getReader(), DiscountCard.class);
            discountCardDao.save(discountCard);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            writer.write(JsonUtil.toJson(discountCard));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String idParam = req.getParameter("id");

        try (Connection connection = connectionPoolManager.getConnection();
             PrintWriter writer = resp.getWriter()) {
            discountCardDao = new DiscountCardDao(connection);
            resp.setContentType("application/json");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Long id = Long.parseLong(idParam);
            DiscountCard discountCard = JsonUtil.fromJson(req.getReader(), DiscountCard.class);
            discountCard.setId(id);
            discountCardDao.update(discountCard);
            writer.write(JsonUtil.toJson(discountCard));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String idParam = req.getParameter("id");

        try (Connection connection = connectionPoolManager.getConnection();
             PrintWriter writer = resp.getWriter()) {
            discountCardDao = new DiscountCardDao(connection);
            resp.setContentType("application/json");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int id = Integer.parseInt(idParam);
            discountCardDao.delete(id);
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


