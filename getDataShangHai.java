package GetSecuritiesData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static GetSecuritiesData.SpildersTools.*;
import static GetSecuritiesData.SpildersTools.split;

public class getDataShangHai {
    public static int i=0;
    private static final List<String> allCode = new ArrayList<>(),allCode_Zhai = new ArrayList<>(),allCode_zhi=new ArrayList<>();
    private static final Map<String,String[]> dailydata = new HashMap<>(), dailydata_zhai = new HashMap<>(),dailydata_zhi=new HashMap<>();

    public static void main(String[] args) throws SQLException {
        LinkDataBase.DropAll("上海全览_后复权");
        getDailyAllData();
        getHistoryIntoDatabase();
        System.out.println("共插入"+i+"个数据");
    }
    public static void getHistoryIntoDatabase() throws SQLException {
        List<String[]> ippool = ipPool.getPoolListInDatabase();
        if(ippool ==null){
            System.out.println("未连接到ip代理池.");
            return;
        }
        int ip_idx = 0,size = ippool.size();
        List<String[]> data,data_zhai,data_zhi;
        for(String code:allCode){
            String url = "http://24.push2his.eastmoney.com/api/qt/stock/kline/get?cb=jQuery112407737056881861861_1642299510895&secid=1."+code+"&ut=fa5fd1943c7b386f172d6893dbfba10b&fields1=f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61&klt=101&fqt=0&end=20500101&lmt=5319&_=1642299510970";
            List<String[]> history = new ArrayList<>();
            do {
                System.out.println("当前的ip为:"+ ippool.get(ip_idx % size)[0]+"   port:"+ ippool.get(ip_idx % size)[1]);
                data = getHistoryWithCode(url, ippool.get(ip_idx % size)[0], Integer.parseInt(ippool.get(ip_idx % size)[1]));
                ip_idx++;
            } while (data == null);
            String[] history_data = new String[12];
            String[] td= dailydata.get(code);
            history_data[0]=code;
            history_data[2]=td[0];
            history_data[3]=td[11];
            history_data[10]="股票";
            history_data[11]="NO";
            for(String[] strings:data){
                history_data[1]=strings[0];history_data[4]=strings[1];history_data[5]=strings[3];history_data[6]=strings[4];history_data[7]=strings[2];
                history_data[8]=strings[5];history_data[9]=strings[6];
                history.add(Arrays.copyOf(history_data,history_data.length));
            }
            if(history.size()==0)history.add(history_data);
            LinkDataBase.addVals(history,"上海全览_不复权","Data","Ccode","Ctime","Cname","Ctype","Copen","Chigh","Clow","Cpreclose","Cvolume","Ctransactions","Mtype","IsGang");
            i++;
        }
        for(String code:allCode_Zhai){
            String url ="https://push2his.eastmoney.com/api/qt/stock/kline/get?secid=1."+code+"&klt=101&fqt=1&lmt=66&end=20500000&iscca=1&fields1=f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6%2Cf7%2Cf8&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61%2Cf62%2Cf63%2Cf64&ut=f057cbcbce2a86e2866ab8877db1d059&forcect=1";
            List<String[]> history_zhai = new ArrayList<>();
            do {
                System.out.println("当前的ip为:"+ ippool.get(ip_idx % size)[0]+"   port:"+ ippool.get(ip_idx % size)[1]);
                data_zhai = getHistoryWithCode(url, ippool.get(ip_idx % size)[0], Integer.parseInt(ippool.get(ip_idx % size)[1]));
                ip_idx++;
            } while (data_zhai == null);
            String[] history_data = new String[12];
            String[] td= dailydata_zhai.get(code);
            history_data[0]=code;
            history_data[2]=td[0];
            history_data[3]=td[11];
            history_data[10]="债券";
            history_data[11]="NO";
            for(String[] strings:data_zhai){
                history_data[1]=strings[0];history_data[4]=strings[1];history_data[5]=strings[3];history_data[6]=strings[4];
                history_data[7]=strings[2];history_data[8]=strings[5];history_data[9]=strings[6];
                history_zhai.add(Arrays.copyOf(history_data,history_data.length));
            }
            if(history_zhai.size()==0)history_zhai.add(history_data);
            LinkDataBase.addVals(history_zhai,"上海全览_不复权","Data","Ccode","Ctime","Cname","Ctype","Copen","Chigh","Clow","Cpreclose","Cvolume","Ctransactions","Mtype","IsGang");
            i++;
        }
        for(String code:allCode_zhi){
            String url = "http://68.push2his.eastmoney.com/api/qt/stock/kline/get?cb=jQuery35109966627811823283_1642301743725&secid=1."+code+"&ut=fa5fd1943c7b386f172d6893dbfba10b&fields1=f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61&klt=101&fqt=1&end=20500101&lmt=7655&_=1642301743885";
            List<String[]> history_zhi = new ArrayList<>();
            do{
                System.out.println("当前的ip为:"+ ippool.get(ip_idx % size)[0]+"   port:"+ ippool.get(ip_idx % size)[1]);
                data_zhi= getHistoryWithCode(url, ippool.get(ip_idx % size)[0], Integer.parseInt(ippool.get(ip_idx % size)[1]));
                ip_idx++;
            }while (data_zhi==null);
            String[] history_data = new String[12];
            String[] td= dailydata_zhi.get(code);
            history_data[0]=code;
            history_data[2]=td[0];
            history_data[3]=td[11];
            history_data[10]="指数";
            history_data[11]="NO";
            for(String[] strings:data_zhi){
                history_data[1]=strings[0];history_data[4]=strings[3];history_data[5]=strings[3];history_data[6]=strings[4];
                history_data[7]=strings[2];history_data[8]=strings[5];history_data[9]=strings[6];
                history_zhi.add(Arrays.copyOf(history_data,history_data.length));
            }
            if(history_zhi.size()==0)history_zhi.add(history_data);
            LinkDataBase.addVals(history_zhi,"上海全览_不复权","Data","Ccode","Ctime","Cname","Ctype","Copen","Chigh","Clow","Cpreclose","Cvolume","Ctransactions","Mtype","IsGang");
            i++;
        }
    }
    public static List<String[]> getHistoryWithCode(String url,String ip,int port){
        String page = getTextByUrlInDiffIp(url, ip, port);
        if (page != null) {
            return split(page, "\",\"");
        }
        return null;

    }
    private static void getDailyData(Map<String,String[]> dailydata,String url,List<String> allCode,String type){
        String page = getTextByUrl(url);
        /*
        f1:未知
        f2:最新价格
        f3:涨跌幅
        f4：涨跌额
        f5：成交量
        f6:成交额
        f7：震幅
        f8：换手率
        f9：市盈率
        f10：量比
        f11：涨速
        f12:股票代码
        f14:股票名称
        f15：最高
        f16：最低
        f17：开盘
        f18：昨收
         */
        if (page == null) {
            System.out.println("未获取到文本");
            return;
        }
        List<String[]> split = split(page, "},\\{");
        if(split==null)return;
        for (String[] strings : split) {
            Map<String, String> tmap = new HashMap<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (String str : strings) {
                String[] tmp = str.split(":");
                tmap.put(tmp[0], tmp[1]);
            }
            if(type.equals("上海B股")&&!tmap.get("f12").startsWith("9"))continue;
            allCode.add(tmap.get("f12"));
            String[] target = new String[]{"f14", "", "f17", "f15", "f16", "f2", "f18", "f3", "f5", "f6", "f4"};
            String[] data = new String[12];
            for (int i = 0; i < 11; i++) {
                if (i == 1) data[1] = simpleDateFormat.format(new Date());
                else data[i] = tmap.get(target[i]);
            }
            data[11]=type;
            dailydata.put(tmap.get("f12"), data);
        }
    }
    public static void getDailyAllData(){
        getDailyData(dailydata,"http://25.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124024357986294572864_1642217390903&pn=1&pz=10000&po=0&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f12&fs=m:1+t:2,m:1+t:23&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152&_=1642217393330",allCode,"上海A股");
        getDailyData(dailydata,"http://25.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124024357986294572864_1642217390907&pn=1&pz=10000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:0+t:7,m:1+t:3&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152&_=1642217392991",allCode,"上海B股");
        getDailyData(dailydata_zhai,"http://25.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124024357986294572864_1642217390907&pn=1&pz=10000&po=0&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f12&fs=m:1+t:4&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152&_=1642217393028",allCode_Zhai,"上海债券");
        getDailyData(dailydata_zhi,"http://30.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124021227096551070113_1642300992460&pn=1&pz=1000&po=0&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f12&fs=m:1+s:2&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152&_=1642300992716",allCode_zhi,"上海指数");
    }
}
