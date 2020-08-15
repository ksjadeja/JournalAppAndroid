package com.journalapp.mail;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sun.mail.smtp.SMTPAddressFailedException;

import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender extends AsyncTask<Void,Void,Void> {

    private String mailhost = "smtp.gmail.com";

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private Session session;

    private String email;
    private String subject;
    private String message;
    private ProgressDialog progressDialog;

    static {
        Security.addProvider(new com.journalapp.mail.JSSEProvider());
    }
    public MailSender(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Sending Email","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();

        Toast.makeText(context,"Message Sent", Toast.LENGTH_LONG).show();

    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(Config.EMAIL,Config.PASSWORD);
            }
        });
        try{
            MimeMessage mimeMessage = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(message.getBytes(), "text/plain"));
            mimeMessage.setSender(new InternetAddress("ksjadeja2812.2017@gmail.com"));
            mimeMessage.setSubject(subject);
            mimeMessage.setDataHandler(handler);

            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email));

            Transport.send(mimeMessage);
            Log.i("MAILSTATUS:","success in Gmail Sender");
        } catch(Exception e){
            if(e.getClass().equals(SMTPAddressFailedException.class)){
                Log.i("MAIL::STATUS","ERROR, Invalid email");
                Toast.makeText(context, "Invalid email address format ", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i("MAILSTATUS:","sending cancelled");
    }
}
