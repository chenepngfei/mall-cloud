package cn.afei.mall.framework.jpa.config;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 【临时诊断工具】包装 DataSource，拦截 Connection.setReadOnly 调用并打印调用栈。
 * 用于定位 "cannot execute INSERT in a read-only transaction" 的只读标志来源。
 * 通过 -Dmall.jpa.readOnlyDiagnostic=true 激活；问题定位后删除。
 */
public class ReadOnlyDiagnosticDataSource implements DataSource {

    private final DataSource delegate;

    public ReadOnlyDiagnosticDataSource(DataSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrap(delegate.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrap(delegate.getConnection(username, password));
    }

    private Connection wrap(Connection connection) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getName().equals("setReadOnly")) {
                boolean autoCommit;
                try {
                    autoCommit = connection.getAutoCommit();
                } catch (SQLException e) {
                    autoCommit = false;
                }
                System.out.println("[READONLY-DIAG] setReadOnly(" + args[0] + ") on " + connection
                        + " autoCommit=" + autoCommit);
                new Exception("[READONLY-DIAG] stack").printStackTrace(System.out);
            }
            return method.invoke(connection, args);
        };
        return (Connection) Proxy.newProxyInstance(
                ReadOnlyDiagnosticDataSource.class.getClassLoader(),
                new Class<?>[]{Connection.class}, handler);
    }

    // ========== 以下全部直接委托 ==========

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        return delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || delegate.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegate.getParentLogger();
    }

}
