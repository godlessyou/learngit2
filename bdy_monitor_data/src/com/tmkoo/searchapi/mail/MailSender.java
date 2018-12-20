package com.tmkoo.searchapi.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;



public class MailSender {

	private final static Logger logger = Logger.getLogger(MailSender.class);
	
	
	// 创建邮件基本信息
	public static MailSenderInfo createMailInfo(String userName, String password, String toAddress, String subject, String content, List<String> attachFileList ) throws Exception {

		String serverHost = "smtp.mxhichina.com";
		String serverPort = "25";
		
		String fromAddress = userName;	

		MailSenderInfo mailSenderInfo = new MailSenderInfo();
		mailSenderInfo.setMailServerHost(serverHost);
		mailSenderInfo.setMailServerPort(serverPort);
		if (toAddress!=null){
			mailSenderInfo.setToAddress(toAddress);
		}
		mailSenderInfo.setFromAddress(fromAddress);
		mailSenderInfo.setUserName(userName);
		mailSenderInfo.setPassword(password);

		mailSenderInfo.setSubject(subject);

		mailSenderInfo.setContent(content);

		mailSenderInfo.setValidate(true);
		
		int size=attachFileList.size();
		
		String[] fileNames = attachFileList.toArray(new String[size]);

		mailSenderInfo.setAttachFileNames(fileNames);

		return mailSenderInfo;
	}
	
	

	// 创建包含wpm案件数据的邮件
	public static MailSenderInfo createDataMail(String subject, String httpLink, List<String> attachFileList) throws Exception {

		
		String userName = "service@ipshine.com";
		
		String password = "Pass_111111";	

		
		// 邮件正文
		String content = httpLink;
		
		MailSenderInfo mailSenderInfo=createMailInfo(userName, password, null, subject, content, attachFileList);

		return mailSenderInfo;
	}
	
	
	
	
	
	// 发送邮件
	public static void sendMail(String subject, String httpLink, List<String> mailToList, List<String> attachFileList) throws Exception {

		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		
		MailSenderInfo mailSenderInfo = null;

		// 创建包含wpm案件数据的邮件
		mailSenderInfo = createDataMail(subject, httpLink, attachFileList);
		
		String userName = mailSenderInfo.getUserName();
		String password = mailSenderInfo.getPassword();

		Properties pro = mailSenderInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailSenderInfo.isValidate()) {
			authenticator = new MyAuthenticator(userName, password);
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		FileInputStream fis = null;
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);

			// 发件人
			String fromMail = mailSenderInfo.getFromAddress();
			if (fromMail != null && !fromMail.equals("")) {
				Address from = new InternetAddress(fromMail);
				mailMessage.setFrom(from);
			}

			// 收件人
//			String mail_to = mailSenderInfo.getToAddress();
			if (mailToList != null &&mailToList.size()>0) {
				for (String mail_to: mailToList){				
					Address to = new InternetAddress(mail_to);
					mailMessage.addRecipient(Message.RecipientType.TO, to);				
				}
			}

			// 设置邮件消息的主题
			mailMessage.setSubject(mailSenderInfo.getSubject());
			
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());

			// 创建html格式邮件
			createHtmlMail(mailSenderInfo, mailMessage);

			// 发送邮件
			Transport.send(mailMessage);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				// throw new RuntimeException(e);
			}
		}
		logger.info("mail send success.");
	}

	
	
	
	// 创建html格式邮件
	private static void createHtmlMail(MailSenderInfo mailSenderInfo,
			Message mailMessage) throws MessagingException {
		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart mainPart = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
		BodyPart html = new MimeBodyPart();
		// 设置HTML内容
		html.setContent(mailSenderInfo.getContent(), "text/html; charset=utf-8");
		mainPart.addBodyPart(html);
		// 将MiniMultipart对象设置为邮件内容
		mailMessage.setContent(mainPart);

		String[] attachFileNames = mailSenderInfo.getAttachFileNames();

		// 为邮件增加附件
		addAttachement(mainPart, attachFileNames);
	}

	
	
	// 为邮件增加附件
	private static void addAttachement(Multipart mainPart,
			String[] attachFileNames) {

		if (attachFileNames == null) {
			return;
		}

		for (String filePath : attachFileNames) {
			File file = new File(filePath);
			// 如果附件存在，就将附件加到邮件中
			if (file.exists()) {
				try {
					MimeBodyPart attachment01 = createAttachment(file);
					// 将附件加入到mainPart
					mainPart.addBodyPart(attachment01);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// logger.info("attachment file  has been added in mail complete.");
				logger.debug("attachment file: " + filePath);
			} else {
				logger.info("attachment file: " + filePath + " is not exist.");
			}
		}

	}
	
	
	

	/**
	 * 根据传入的文件创建附件并返回
	 */
	private static MimeBodyPart createAttachment(File file) throws Exception {
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.attachFile(file);
		String temp = attachmentPart.getFileName();
		attachmentPart.setFileName(MimeUtility.encodeText(temp, "UTF-8", null));
		// FileDataSource fds = new FileDataSource(fileName);
		// attachmentPart.setDataHandler(new DataHandler(fds));
		// attachmentPart.setFileName(fds.getName());

		return attachmentPart;
	}

	public static void main(String[] args) throws Exception {
		String subject="1558期送达公告(发文日期：2017-07-06)";
		String httpLink="http://www.ip-manager.top/hgj_pages/hint_arrivals.html";
		List<String> attachFileList=new ArrayList<String> ();
		attachFileList.add("C:/gonggao/1558_送达公告_01.jpg");
		List<String> mailToList=new ArrayList<String> ();
		mailToList.add("michael2080@126.com");
		sendMail(subject, httpLink, mailToList, attachFileList);

	}

}