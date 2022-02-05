package GetSecuritiesData;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ipPool {
    static int total = 0;
    //更新代理池，在LinkDatabase中调用
    public  void  setPoolList(String url){
        LinkDataBase.DropAll("ippool");
        List<String[]> pool = new ArrayList<>();
        try {
            pool = ipPool.getIpPortPool(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkDataBase.addVals(pool,"ippool","Data","ip","port");
    }
    //返回从数据库中获得代理池
    public static List<String[]> getPoolListInDatabase() throws SQLException {
        Statement statement = LinkDataBase.getStatementByDataBaseName("Data");
        ResultSet resultSet;
        List<String[]> pool = new ArrayList<>();
        try {
            if (statement != null) {
                resultSet = statement.executeQuery("select * from ippool");
            }else return null;
        } catch (SQLException e) {
            System.out.println("未取得ip代理池");
            return null;
        }
        while(resultSet.next()){
            String ip = resultSet.getString("ip"),port =  resultSet.getString("port");
            pool.add(new String[]{ip,port});
            System.out.println("获取 ip:"+ip+" port:"+port);
        }
        return pool;
    }
    private static final Set<String[]> pool = new HashSet<>();
    private static List<String[]>  getIpPortPool(String url) throws IOException {
        System.out.println("抓取ip与port");
        getIpPool();
        System.out.println("筛查中，等待挺久的。半小时一次");
        filterIp(url);
        return new ArrayList<>(pool);
    }
    //爬取西刺代理池（不讲武德）
   private static void getIpPool(){
        WebClient webClient = new WebClient(BrowserVersion.EDGE);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(true);
        webClient.setJavaScriptEngine(new getDataInShangHai.MyJavaScriptEngine(webClient));
        webClient.setJavaScriptErrorListener(new getDataInShangHai.MyJsError());
        webClient.waitForBackgroundJavaScript(3000);
        webClient.setIncorrectnessListener((s, o) -> {});
        String url = "https://ip.ihuan.me/address/5Lit5Zu9.html";
        boolean is_end = true;
        Document doc;
       HtmlPage page;
       try {
           page = webClient.getPage(url);
       } catch (IOException e) {
           System.err.println("网页连接失败，请检查正确的url。Error:"+url);
           return;
       }
       while (is_end) {
            doc = Jsoup.parse(page.asXml());
            Elements ports = doc.getElementsByTag("td"), ips = doc.select("td a[target='_blank']");
            if (ips.size() == 0) {
                is_end = false;
            }
            for (int j = 0; j < ips.size(); j++) {
                double time = Double.parseDouble(ports.get(10 * j + 7).ownText().replace("秒", ""));
                String ip = ips.get(j).ownText(), port = ports.get(10 * j + 1).ownText();
                if (!ip.equals("") && !port.equals("")) {
                    System.out.println("获取代理ip:" + ips.get(j).ownText() + "   代理端口号：" + ports.get(10 * j + 1).ownText() + "     连接时间： " + time+"s");
                    pool.add(new String[]{ip, port});
                    total++;
                }
            }
            List<HtmlAnchor> nextE = page.getByXPath("//a[@aria-label=\"Next\"]");
            if (nextE.size()==0)break;
            else {
                try {
                    page = nextE.get(0).click();
                } catch (IOException e) {
                    System.out.println("未获取到下一页按钮。");
                    return;
                }
            }

        }
    }
    //更具url过滤ip，新爬一个网站建议执行更新代理池
    private static void filterIp(String url){
        HttpClient httpClient = HttpClients.createDefault();
        Set<String[]> tmp = new HashSet<>();
        for(String[] ipPort:pool){
            HttpHost proxy = new HttpHost(ipPort[0],Integer.parseInt(ipPort[1]));
            HttpGet get =  new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).setProxy(proxy).build();
            get.setConfig(requestConfig);
            HttpResponse response;
            try{
                response = httpClient.execute(get);
            }catch (Exception e) {
                tmp.add(ipPort);
                System.out.println("ip: "+ipPort[0]+"  "+"端口号:"+ipPort[1]+"  弃用"+"    原因：连接超时"+"   剩余："+--total);
                continue;
            }
            if (response.getStatusLine().getStatusCode()!=200){
                System.out.println("ip: "+ipPort[0]+"  "+"端口号:"+ipPort[1]+"  弃用"+"    原因：连接失败"+"   剩余："+--total);
                tmp.add(ipPort);
                continue;
            }
            System.out.println("ip: "+ipPort[0]+"  "+"端口号:"+ipPort[1]+"  入库"+"   剩余："+--total);
            get.abort();
        }
        pool.removeAll(tmp);
    }
}
