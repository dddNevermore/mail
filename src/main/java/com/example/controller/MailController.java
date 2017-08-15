package com.example.controller;import com.example.service.RedisService;import java.io.IOException;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.annotation.Value;import org.springframework.mail.SimpleMailMessage;import org.springframework.mail.javamail.JavaMailSender;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RestController;@RestControllerpublic class MailController  extends BaseController{  @Autowired  private JavaMailSender mailSender;  @Autowired  private RedisService redisService;  @Value("${spring.mail.username}")  private String mailUser;  @Value("${demo.limit.boolean}")  private Boolean limitBoolean;  @Value("${demo.limit.ipCount}")  private int limitIpCount;  @Value("${demo.limit.mailCount}")  private int limitMailCount;  @Value("${demo.limit.limitTime}")  private int limitTime;    @RequestMapping({"/sendMail"})  public String sendSimpleEmail(HttpServletRequest request)  {    String msg = "ok";    String[] to = { "369644469@qq.com" };    if (limitBoolean.booleanValue())    {      String ip = BaseController.getIpAddress(request);      long ipCount = redisService.incr(ip).longValue();      long ipTtl = redisService.ttl(ip);      if (ipTtl == -1L) {        redisService.expire(ip, limitTime);      }      if (ipCount > limitIpCount)      {        msg = "iplimit";        return msg;      }      String[] arrayOfString1;      int j = (arrayOfString1 = to).length;      for (int i = 0; i < j; i++)      {        String mailAdress = arrayOfString1[i];        long mailCount = redisService.incr(mailAdress).longValue();        long mailTtl = redisService.ttl(mailAdress);        if (mailTtl == -1L) {          redisService.expire(mailAdress, limitTime);        }        if (mailCount > Integer.valueOf(limitMailCount).intValue())        {          redisService.decr(ip);          msg = "maillimit";          return msg;        }      }    }    SimpleMailMessage message = new SimpleMailMessage();    message.setFrom(mailUser);    message.setTo(to);    message.setSubject("测试标题");    message.setText("测试内容");    try    {      mailSender.send(message);    }    catch (Exception e)    {      return e.getMessage();    }    return msg;  }    @RequestMapping({"/"})  public void welcome(HttpServletResponse response, Model model)    throws IOException  {    model.addAttribute("port", super.getServerPort());    response.sendRedirect("welcome.html");  }}