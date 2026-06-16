package net.oxizen;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class OxMail {
	public static Builder open(JavaMailSender $mailSender) throws MessagingException {
		return new Builder($mailSender);
	}

	public static class Builder implements Runnable {
		private JavaMailSender mailSender;
		private MimeMessage message;
		private MimeMessageHelper helper;

		private Builder(JavaMailSender $mailSender) throws MessagingException {
			mailSender = $mailSender;
			message = $mailSender.createMimeMessage();
			helper = new MimeMessageHelper(message, true, "UTF-8");
		}

		public Builder title(String $title) throws MessagingException {
			helper.setSubject($title);
			return this;
		}

		public Builder content(String $content) throws MessagingException {
			helper.setText($content, true);
			return this;
		}

		public Builder from(String $from) throws MessagingException, UnsupportedEncodingException {
			InternetAddress from = InternetAddress.parse($from)[0];
			String personal = from.getPersonal();
			if (personal != null) {
				from.setPersonal(personal, "UTF-8");
			}
			helper.setFrom(from);
			return this;
		}

		public Builder to(String $to) throws MessagingException {
			helper.setTo(InternetAddress.parse($to));
			return this;
		}

		public Builder cc(String $cc) throws MessagingException {
			helper.setCc(InternetAddress.parse($cc));
			return this;
		}

		public Builder bcc(String $bcc) throws MessagingException {
			helper.setBcc(InternetAddress.parse($bcc));
			return this;
		}

		public Builder file(String $filename, File $file) throws UnsupportedEncodingException, MessagingException {
			helper.addAttachment(new String($filename.getBytes("KSC5601"), "EUC-KR"), $file);
			return this;
		}

		public void send() {
			Ox.run(this);
		}

		@Override
		public void run() {
			mailSender.send(message);
		}
	}
}
