package it.unibo.sistemiMobile.mybookshelf.StatisticClasses;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;

public class StatisticsProducer {
    private List<BookRead> books = new ArrayList<>();

    public void updateBooksList(List<BookRead> books){
        this.books = books;
    }

    public boolean isMonthlyChallengeStillActive(int monthChallenge){
        return monthChallenge == Calendar.getInstance().get(Calendar.MONTH);
    }

    public boolean isYearChallengeStillActive(int yearChallenge){
        return yearChallenge == Calendar.getInstance().get(Calendar.YEAR);
    }

    public int getAmountOfBooksReadThisMonth(){
        int amount = 0;
        for(int i = 0; i < books.size(); i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(books.get(i).getEndDate());
            Log.i("MY", "Mese libro " + calendar.get(Calendar.MONTH) + " in this month");

            if(calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                    calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)){
                amount++;
            }
        }
        return amount;
    }

    public int getAmountOfBookReadThisYear(){
        int amount = 0;
        for(int i = 0; i < books.size(); i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(books.get(i).getEndDate());
            if(calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)){
                amount++;
            }
        }
        return amount;
    }

    public Map<Integer, Integer> getAmountOfBookReadPerMonth(){
        Map<Integer, Integer> amountPerMonth = new HashMap<>();
        for(int j = 0; j < 12; j++) {
            amountPerMonth.put(j, getAmountOfBookReadInMonth(j));
        }
        return amountPerMonth;
    }

    public int getAmountOfBookReadInMonth(int month){
        int amount = 0;
        for(int i = 0; i < books.size(); i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(books.get(i).getEndDate());
            if(calendar.get(Calendar.MONTH) == month &&
                    calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)){
                amount++;
            }
        }
        return amount;
    }

    public Double monthAverage(){
        double avg = 0;
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        Map<Integer, Integer> amountPerMonth = getAmountOfBookReadPerMonth();
        for(int i = 0; i < thisMonth+1; i++){
            avg += amountPerMonth.get(i);
        }
        BigDecimal bd = BigDecimal.valueOf(avg/(thisMonth+1));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private int olderYear(){
        int older = Calendar.getInstance().get(Calendar.YEAR);
        Calendar calendar = Calendar.getInstance();
        for(int i = 0; i < books.size(); i++){
            calendar.setTimeInMillis(books.get(i).getEndDate());
            if(calendar.get(Calendar.YEAR) < older){
                older = calendar.get(Calendar.YEAR);
            }
        }
        return older;
    }

    public Double yearAverage(){
        double years = Calendar.getInstance().get(Calendar.YEAR) - olderYear();
        double b = books.size();
        BigDecimal bd = BigDecimal.valueOf(b/(years+1));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
