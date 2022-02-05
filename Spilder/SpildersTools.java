package GetSecuritiesData;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpildersTools {
    //网站返回的数据
    //************************************************短时间内爬取多个网站调用，防止封ip
    public static String getTextByUrlInDiffIp(String url,String ip,int port){
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(500).setConnectTimeout(500).setSocketTimeout(500).setProxy(new HttpHost(ip,port)).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        HttpResponse response;
        HttpClient webclient = HttpClients.createMinimal();
        try {
            response = webclient.execute(httpGet);
        } catch (IOException e) {
            System.out.println("网站没连上，请检查url是否正确");
            return null;
        }
        if(response.getStatusLine().getStatusCode()==404||response.getStatusLine().getStatusCode()==500){
            System.out.println("网站不存在");
            return null;
        }
        try {
            return EntityUtils.toString(response.getEntity(),"utf8");
        } catch (IOException e) {
            System.out.println("网站失联");
            return null;
        }
    }
    //********************************************一次性返回文本
    public static String getTextByUrl(String url) {
        HttpClient webclient =  HttpClients.createMinimal();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response ;
        try {
            response = webclient.execute(httpGet);
        } catch (IOException e) {
            System.out.println("检查url正确性");
            e.printStackTrace();
            return null;
        }
        if(response.getStatusLine().getStatusCode()==404||response.getStatusLine().getStatusCode()==500) {
            System.out.println("网站不存在");
            return null;
        }//网站不存在
        try {
            return EntityUtils.toString(response.getEntity(),"utf8");
        } catch (IOException e) {
            System.out.println("未访问到网站");
            e.printStackTrace();
            return null;
        }
    }
    //找到第一个文本中的中括号
    public static int getFirstKuoHao(String str){
        int l= 0;
        while(l!=str.length()&&str.charAt(l)!='[')l++;
        return l==str.length()?0:l+1;
    }
    //找到文本最后一个括号
    public static int getLastKuoHao(String str){
        int r = str.length()-1;
        while(r!=-1&&str.charAt(r)!=']')r--;
        return r==-1?str.length():r;
    }
    //获取需要使用的数据（根据下标）
    public static String[] get_ByNeed(String[] ordi,int ... index){
        String[] ret = new String[index.length];
        int idx=0;
        for(int i:index){
            ret[idx++] = ordi[i];
        }
        return ret;
    }
    //分割括号内容page，必须是括号内容包围的！！像[[....],[.....]]
    public static String[][] split(String text,int inLen){
        if(text.contains("<")||text.contains(">")){
            System.out.println("ip连入错误。");
            return null;
        }
        int FirstK = getFirstKuoHao(text),LastK=getLastKuoHao(text);
        String[] split = text.substring(FirstK,LastK).split("],");
        String[][] ret = new String[split.length][inLen];
        for(int i=0;i<split.length;i++) {
            split[i] = split[i].replace("[","");
            ret[i] = split[i].split(",");
        }
        return ret;
    }
    //改：根据regex分割内容
    public static List<String[]> split(String str, String regex){
        if(str.contains("<")||str.contains(">")){
            System.out.println("ip连入错误");
            return null;
        }
        if(!regex.contains("\""))
            str = str.replaceAll("\"","");//将"过滤掉，输出整洁
        int start = getFirstKuoHao(str)+1, last = getLastKuoHao(str)-1;
        if(start>last)return new ArrayList<>();
        String[] all_values = str.substring(start,last).split(regex);
        List<String[]> ret = new ArrayList<>();
        for (String all_value : all_values) {
            ret.add(all_value.split(","));
        }
        return ret;
    }
}
