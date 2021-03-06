package GetSecuritiesData;
//顾名思义，链接数据库
//有破解的数据库图形化管理软件，地址放这里：https://cloud.tencent.com/developer/article/1804255，按着要求来就好了，省了几百块
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class LinkDataBase {
    //更新ip代理池
    public static void updateIpPool(String url){
       new ipPool().setPoolList(url);
    }
    //通过List向数据库添加记录,可变参数是数据库的列名，要和string[]一一对应！
    public static void addVals(List<String[]> needAdd,String tableName,String databaseName,String... keys){
        Statement statement = getStatementByDataBaseName(databaseName);
        int len = needAdd.get(0).length;
        for(int j= needAdd.size()-1;j>=0;j--){
            String[] val = needAdd.get(j);
            StringBuilder values = new StringBuilder(),Keys = new StringBuilder();
            for(int i=0;i<len;i++){
                if (i!=len-1) {
                    values.append("'").append(val[i]).append("'").append(",");
                    Keys.append(keys[i]).append(",");
                }
                else {
                    values.append("'").append(val[i].equals("")?"-":val[i]).append("'");
                    Keys.append(keys[i]);
                }
            }
            try {
                if (statement != null) {
                    //System.out.println("insert into "+tableName+" ("+Keys+") "+ "values " +"("+values+");");
                    statement.executeUpdate("insert into "+tableName+" ("+Keys+") "+ "values " +"("+values+");");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                System.out.println("插入失败");
                return;
            }
            /*
            System.out.print("插入 ");
            for (int i=0;i<len;i++){
                System.out.print(keys[i]+":"+val[i]+(i!=len-1?", ":""));
            }
            System.out.println("   成功");*/
        }
    }
    //删除数据库的tableName表
    public static void DropAll(String tableName){
        Statement statement = LinkDataBase.getStatementByDataBaseName("Data");
        try {
            if (statement != null) {
                statement.executeUpdate("TRUNCATE TABLE "+tableName+";");
            }else {
              System.err.println("未找到数据库");
            }
        } catch (SQLException e) {
            System.out.println("删除失败");
            e.printStackTrace();
        }
    }
    public static Statement getStatementByDataBaseName(String databasename){
        //连接mysql  根据需要改localhost和端口号,比如要从本地更改服务器的数据库
        String url = "jdbc:mysql://localhost:3306/"+databasename+"?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
        try {
            return Objects.requireNonNull(Link(url)).createStatement();
        } catch (SQLException e) {
            System.out.println("创建操作对象失败.");
            return null;
        }
    }
    /**
     * @param url 本地数据库使用：jdbc:mysql://localhost:3306/数据库名称
     * @return  连接对象
     */
    private static Connection Link(String url){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("注册驱动失败。");
            return null;
        }
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, "root", "lyp057403");
        } catch (SQLException e) {
            System.out.println("连接数据库失败");
            return null;
        }
        System.out.println("连接数据库：  "+url+"  成功");
        return connection;
    }
}
