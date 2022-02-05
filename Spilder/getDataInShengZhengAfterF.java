package GetSecuritiesData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static GetSecuritiesData.SpildersTools.*;
public class getDataInShengZhengAfterF {
    private static final List<String> allCode = new ArrayList<>(),allCode_Zhai = new ArrayList<>(),allCode_zhiR=new ArrayList<>(),allCode_zhiG = new ArrayList<>();
    private static final Map<String,String[]> dailydata = new HashMap<>(), dailydata_zhai = new HashMap<>(),dailydata_zhiG=new HashMap<>(),dailydata_zhiR=new HashMap<>();

    public static void main(String[] args) throws SQLException {
       getDailyAllData();
        getHistoryIntoDatabase();
    }
    public static void getHistoryIntoDatabase() throws SQLException {
        List<String[]> ippool = ipPool.getPoolListInDatabase();
        if(ippool ==null){
            System.out.println("未连接到ip代理池.");
            return;
        }
        int ip_idx = 0,size = ippool.size();
        List<String[]> data,data_zhai,data_zhiG,data_zhiR;
        for(String code:allCode){
            String url = "http://push2his.eastmoney.com/api/qt/stock/kline/get?cb=jQuery112405061615361789258_1641902823737&fields1=f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61&ut=7eea3edcaed734bea9cbfc24409ed989&klt=101&fqt=2&secid=0."+code+"&beg=0&end=20500000&_=1641902823741";
            List<String[]> history = new ArrayList<>();
            do {
                System.out.println("当前的ip为:"+ ippool.get(ip_idx % size)[0]+"   port:"+ ippool.get(ip_idx % size)[1]);
                data = getHistoryWithCode(url, ippool.get(ip_idx % size)[0], Integer.parseInt(ippool.get(ip_idx % size)[1]));
                ip_idx++;
            } while (data == null);
            String[] history_data = new String[12];
            String[] td= dailydata.get(code);
            history_data[0]=code;
            String rep = td[0].replaceAll("'"," ");
            history_data[2]=rep;
            history_data[3]=td[11];
            history_data[10]="股票";
            history_data[11]="NO";
            for(String[] strings:data){
                history_data[1]=strings[0];history_data[4]=strings[1];history_data[5]=strings[3];history_data[6]=strings[4];history_data[7]=strings[2];
                history_data[8]=strings[5];history_data[9]=strings[6];
                history.add(Arrays.copyOf(history_data,history_data.length));
            }
            if(history.size()==0)history.add(history_data);
            System.out.println(rep+" "+history_data[0]);
           if(!rep.equals(td[0])){
               LinkDataBase.addVals(history,"深圳全览_后复权","Data","Ccode","Ctime","Cname","Ctype","Copen","Chigh","Clow","Cpreclose","Cvolume","Ctransactions","Mtype","IsGang");
           }
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
        /* String[] urls = new String[]{"http://push2his.eastmoney.com/api/qt/stock/kline/get?cb=jQuery112405061615361789258_1641902823737&fields1=f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61&ut=7eea3edcaed734bea9cbfc24409ed989&klt=101&fqt=1&secid=0."+code+"&beg=0&end=20500000&_=1641902823741"
            ,"https://push2his.eastmoney.com/api/qt/stock/kline/get?secid=0.123060&klt=101&fqt=1&lmt=66&end=20500000&iscca=1&fields1=f1%2Cf2%2Cf3%2Cf4%2Cf5%2Cf6%2Cf7%2Cf8&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61%2Cf62%2Cf63%2Cf64&ut=f057cbcbce2a86e2866ab8877db1d059&forcect=1"};
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
            if(type.equals("深圳B股")&&tmap.get("f12").startsWith("9"))continue;
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
    private static void getDailyDataInZhiShu(){
        String page = getTextByUrl("http://65.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112405353884444796773_1642053280272&pn=1&pz=10000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:0+t:5&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152&_=1642053280273");
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
            String[] target = new String[]{"f14", "", "f17", "f15", "f16", "f2", "f18", "f3", "f5", "f6", "f4"};
            String[] data = new String[13];
            for (int i = 0; i < 11; i++) {
                if (i == 1) data[i] = simpleDateFormat.format(new Date());
                else data[i] = tmap.get(target[i]);
            }
            data[11]= "深圳指数";
            if(data[0].contains("港币")){
                data[12]="YES";
                getDataInShengZhengAfterF.allCode_zhiG.add(tmap.get("f12"));
                getDataInShengZhengAfterF.dailydata_zhiG.put(tmap.get("f12"),data);
            }else {
                data[12]="NO";
                getDataInShengZhengAfterF.allCode_zhiR.add(tmap.get("f12"));
                getDataInShengZhengAfterF.dailydata_zhiR.put(tmap.get("f12"),data);
            }
        }
    }
    public static void putDailyDataIntoDB(){
        getDailyAllData();
        List<String[]> all = getDailyData(dailydata_zhai);
        all.addAll(getDailyData(dailydata));
        all.addAll(getDailyData(dailydata_zhiG));
        all.addAll(getDailyData(dailydata_zhiR));
        LinkDataBase.addVals(all,"深圳每日最新全览","Data","Ccode","Cname","Ctime","Copen","Chigh","Clow","Clast","Cpreclose","Cchg_rate","Cvolume","Ctransactions","Cchg","Mtype","IsGang");
        //LinkDataBase.addVals(all,"深圳全览","Data","Ccode","Cname","Ctime","Copen","Chigh","Clow","Clast","Cpreclose","Cchg_rate","Cvolume","Ctransactions","Cchg","Mtype","IsGang");
    }
    private static List<String[]> getDailyData(Map<String,String[]> map){
        LinkDataBase.DropAll("深圳每日最新全览");
        List<Map.Entry<String,String[]> > list = new ArrayList<>(map.entrySet());
        List<String[]> ret = new ArrayList<>();
        for(Map.Entry<String ,String[]> entry:list){
            String[] t = new String[14];
            t[0] = entry.getKey();
            String[] strings = entry.getValue();
            if(strings[5].equals("-"))continue;
            System.arraycopy(strings, 0, t, 1, t.length - 2);
            t[13] = t[1].contains("港币")?"YES":"NO";
            ret.add(t);
        }
        return ret;
    }
    public static void getDailyAllData(){
        getDailyData(dailydata,"http://79.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112406776500846875879_1641969638091&pn=1&pz=59&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:0+t:7,m:1+t:3&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152&_=1641969638092",allCode,"深圳B股");
        getDailyData(dailydata,"http://40.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124011139070059343359_1641962140686&pn=1&pz=100000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:0+t:6,m:0+t:80&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152&_=1641962140687",allCode,"深圳A股");
        getDailyData(dailydata_zhai,"http://46.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112404270472651434769_1641978145036&pn=1&pz=10000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:0+t:8&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152&_=1641978145037",allCode_Zhai,"深圳债券");
        getDailyDataInZhiShu();
    }
}
