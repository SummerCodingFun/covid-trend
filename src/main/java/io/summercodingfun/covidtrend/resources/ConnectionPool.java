package io.summercodingfun.covidtrend.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Stack;

public class ConnectionPool {
    private String url;
    private String userName;
    private String password;
    private int maxPoolSize = 10;
    private int connNum = 0;
    private static final String sql_verifyConn = "select 1";

    Stack<Connection> freePool = new Stack<>();
    Stack<Connection> occupiedPool = new Stack<>();

    public ConnectionPool(String url, String userName, String password, int maxSize) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.maxPoolSize = maxSize;
    }

    public synchronized Connection getConnection() throws SQLException {
        Connection conn = null;
        if (isFull()) {
            throw new SQLException("the connection pool is full");
        }
        conn = getConnectionFromPool();

        if (conn == null) {
            conn = createNewConnectionForPool();
        }

        conn = makeAvailable(conn);
        return conn;
    }

    public synchronized void returnConnection(Connection conn) throws SQLException {
        if (conn == null) {
            throw new NullPointerException();
        }
        if (!occupiedPool.remove(conn)) {
            throw new SQLException("the connection is returned already or it isn't for this pool");
        }
        freePool.push(conn);
    }

    private synchronized boolean isFull() {
        return ((freePool.size() == 0) && (connNum >= maxPoolSize));
    }

    private Connection createNewConnectionForPool() throws SQLException {
        Connection conn = createNewConnection();
        connNum++;
        occupiedPool.add(conn);
        return conn;
    }

    private Connection createNewConnection() throws SQLException {
        Connection conn = null;
        conn = DriverManager.getConnection(url, userName, password);
        return conn;
    }

    private Connection getConnectionFromPool() {
        Connection conn = null;
        if (freePool.size() > 0) {
            conn = freePool.pop();
            occupiedPool.add(conn);
        }
        return conn;
    }

    private Connection makeAvailable(Connection conn) throws SQLException {
        if (isConnectionAvailable(conn)) {
            return conn;
        }
        occupiedPool.remove(conn);
        connNum--;
        conn.close();

        conn = createNewConnection();
        occupiedPool.add(conn);
        connNum++;
        return conn;
    }

    private boolean isConnectionAvailable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeQuery(sql_verifyConn);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
