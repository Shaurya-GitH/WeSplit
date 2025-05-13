package com.wesplit.main.services;

import com.wesplit.main.exceptions.ExternalAPIFailedException;
import com.wesplit.main.payloads.BalanceDTO;
import com.wesplit.main.payloads.CurrencyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class CurrencyServiceImpl implements CurrencyService{

    //balances are stored in INR
    //balances are displayed in the preferred currency using cookies
    //expenses are saved and displayed in the original currency

    RestTemplate restTemplate;

    @Autowired
    CurrencyServiceImpl(RestTemplateBuilder builder){
        this.restTemplate=builder.build();
    }

    @Value("${myapp.currency_API}")
    String currencyAPI;


    @Override
    public BigDecimal updateConversion(BigDecimal money, String from) {
        String finalAPI= currencyAPI.replace("FROM",from).replace("TO","INR");
        ResponseEntity<CurrencyResponse> response= restTemplate.exchange(finalAPI,HttpMethod.GET,null, CurrencyResponse.class);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            BigDecimal multiplier=response.getBody().getData().getMid();
            return money.multiply(multiplier);
        }
        else{
            throw new ExternalAPIFailedException("Currency API failed");
        }
    }

    @Override
    public BalanceDTO displayBalance(BalanceDTO balance, String to) {
        String finalAPI= currencyAPI.replace("FROM","INR").replace("TO",to);
        ResponseEntity<CurrencyResponse> response= restTemplate.exchange(finalAPI,HttpMethod.GET,null, CurrencyResponse.class);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            BigDecimal multiplier=response.getBody().getData().getMid();
            if(balance.getOneOweTwo().compareTo(BigDecimal.ZERO)!=0){
                balance.setOneOweTwo(balance.getOneOweTwo().multiply(multiplier));
            }
            if(balance.getTwoOweOne().compareTo(BigDecimal.ZERO)!=0){
                balance.setTwoOweOne(balance.getTwoOweOne().multiply(multiplier));
            }
            return balance;
        }
        else{
            throw new ExternalAPIFailedException("Currency API failed");
        }
    }
}
