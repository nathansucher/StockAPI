import com.intrinio.api.SecurityApi;
import com.intrinio.invoker.ApiClient;
import com.intrinio.invoker.ApiException;
import com.intrinio.invoker.Configuration;
import com.intrinio.invoker.auth.ApiKeyAuth;
import com.intrinio.models.ApiResponseSecurityStockPrices;
import com.intrinio.models.StockPriceSummary;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StockPrices
{
    private static final String APPL = "AAPL";
    private static final String FREQUENCY = "daily";
    private static final Integer pageSize = 100;
    private static final String nextPage = null;

    private String _authKey = null;
    private ScheduledExecutorService _scheduledExecutorService = null;

    StockPrices()
    {
        readProperties();
        start();
    }

    private void start()
    {
        _scheduledExecutorService = Executors.newScheduledThreadPool(1);
        _scheduledExecutorService.scheduleWithFixedDelay(new StockThread(), 5, 60, TimeUnit.SECONDS);
    }

    private void getStockPrices()
    {
        readProperties();

        ApiClient apiClient = Configuration.getDefaultApiClient();
        ApiKeyAuth authentication = (ApiKeyAuth) apiClient.getAuthentication("ApiKeyAuth");
        authentication.setApiKey(_authKey);

        SecurityApi securityApi = new SecurityApi();

        LocalDate startDate = LocalDate.of(2020, 03, 18);
        LocalDate endDate = LocalDate.now();

        try
        {
            ApiResponseSecurityStockPrices result = securityApi.getSecurityStockPrices(APPL, startDate, endDate, FREQUENCY, pageSize, nextPage);
            List<StockPriceSummary> stockPrices = result.getStockPrices();
            System.out.println(stockPrices);
        } catch (ApiException e)
        {
            System.err.println("Exception when calling SecurityApi#getSecurityStockPrices");
            e.printStackTrace();
        }
    }

    private void readProperties()
    {
        try
        {
            InputStream inputStream = StockPrices.class.getClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            _authKey = properties.getProperty("authKey");
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }
    }


    private class StockThread implements Runnable
    {
        @Override
        public void run()
        {
            getStockPrices();
        }
    }

}
