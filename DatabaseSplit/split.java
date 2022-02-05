//    数据库分表执行函数
    private static void DoSplitList(String tableName,Statement statement){
        List<String[]> get = getGroup(tableName,statement);
        String where = tableName.substring(0,2),isF=tableName.substring(5,8);
        if (get != null) {
            for(String[] val:get){
                SplitList(where,val,isF,tableName,statement);
            }
        }else {
            System.err.println("代码为空!");
        }
    }
//    数据库分表函数
    private static void SplitList(String where,String[] vals,String isF,String tableNmae,Statement statement){
        String NewTableName = where+vals[1]+"_"+vals[0]+"_"+isF;
        String sql = "create table "+NewTableName+" select * from "+tableNmae+" where Ccode='"+vals[0]+"'";
        System.out.println(NewTableName+" sql:"+sql);
        try {
            if(statement==null){
                System.err.println("数据库不存在");
                return;
            }
            statement.execute(sql);
        } catch (SQLException e) {
            System.err.println(sql+"执行错误");
        }
    }
    public static void main(String[] args) throws SQLException {
      setKey();
    }
    /*设置主键*/
    private static void setKey() throws SQLException {
        String[] strs = new String[] {"上海数据","深圳数据","香港数据"};
        for(String s:strs){
            Statement statement = getStatementByDataBaseName(s);
            ResultSet resultSet;
            List<String> tables = new ArrayList<>();
            try {
                assert statement != null;
                resultSet = statement.executeQuery("select table_name from information_schema.tables where table_schema='"+s+"'");
            } catch (SQLException e) {
                e.printStackTrace();
                continue;
            }
            while (true){
                try {
                    if (!resultSet.next()) break;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                  tables.add(resultSet.getString("Table_name"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(tables);
            for(String tableName:tables){
                System.out.println("alter table "+tableName+" add primary key(Ccode,Ctime);");
                statement.executeUpdate("alter table "+tableName+" add primary key(Ccode,Ctime);");
            }
        }
    }
