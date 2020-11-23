package com.example.collegeproject.sendMail;

import android.util.Log;

import com.example.collegeproject.models.GetEventsModel;
import com.example.collegeproject.models.TicketModel;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.Callable;

import javax.mail.MessagingException;

public class SendMailTask implements Callable<Integer> {

    String toAddress;
    GetEventsModel model;
    List<TicketModel> tickets;
    String subject="", body="";

    public SendMailTask(String toAddress, GetEventsModel model, List<TicketModel> tickets) {
        this.toAddress = toAddress;
        this.model = model;
        this.tickets = tickets;
        subject="Booking Confirmation!";
        if(tickets.size()==1) {
            body = "Your ticket has been successfully booked for the event: <b>" + model.getName() + "</b>.<br><br>Your Ticket Id is: " + tickets.get(0).getTicketId();
        }else{
            body = "Your tickets have been successfully booked for the event: <b>" + model.getName() + "</b>.<br><br>Your Ticket Ids are: " + tickets.get(0).getTicketId();
            for(int i=1;i<tickets.size();i++){
                body+=", "+tickets.get(i).getTicketId();
            }
        }
        body+=".<br><br>Tickets will be available on the app and website for proof.<br><br>Thank you and enjoy the event!";
    }

    @Override
    public Integer call() {
        Log.d("SendMailTask", "About to instantiate GMail...");
        MailService androidEmail = new MailService("pictdsy19@gmail.com",
                "PICT@2019", toAddress, subject, body);
        try {
            androidEmail.createEmailMessage();
            androidEmail.sendEmail();
            Log.d("SendMailTask", "Mail Sent.");
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
