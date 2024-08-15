import android.util.Log;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviarCorreo {

    private static final String CORREO_REMITENTE = "cuentaempe03@gmail.com";
    private static final String CONTRASENA = "Sergio 1989";
    private static final String CORREO_DESTINATARIO = "siivforce@gmail.com";

    public static void enviarInformacion(String informacion, String asunto) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CORREO_REMITENTE, CONTRASENA);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(CORREO_REMITENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(CORREO_DESTINATARIO));
            message.setSubject(asunto);
            message.setText(informacion);

            Transport.send(message);

            Log.d("EnvioCorreo", "Correo electrónico enviado exitosamente");
        } catch (MessagingException e) {
            Log.e("EnvioCorreo", "Error al enviar correo electrónico: " + e.getMessage());
            // Puedes mostrar un mensaje de error al usuario si es necesario
        }
    }
}
