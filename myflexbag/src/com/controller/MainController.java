package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.frame.Service;
import com.vo.ItemVO;
import com.vo.UserVO;

@Controller
public class MainController {

	@Resource(name="uservice")
	Service<String, UserVO> uservice;
	
	@Resource(name="iservice")
	Service<String, ItemVO> iservice;
	
	@RequestMapping("/main.mc")
	public ModelAndView main(ModelAndView mv) {
		ItemVO bestVO = new ItemVO();
		ItemVO latestVO = new ItemVO();
		
		ArrayList<ItemVO> bestList = null;
		ArrayList<ItemVO> latestList = null;
		
		bestVO.setPage(1);
		bestVO.setPerPageNum(3);
		bestVO.setSortcon("popularity");
		latestVO.setPage(1);
		latestVO.setPerPageNum(3);
		latestVO.setSortcon("pdate");

		try {
			bestList = iservice.getall(bestVO);
			latestList = iservice.getall(latestVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mv.addObject("best", bestList);
		mv.addObject("latest", latestList);
		mv.setViewName("main");
		return mv;
	}
	
	//�α��� �� ���� �κ�
	@RequestMapping(value = "/login.mc", method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("center", "login");
		mv.setViewName("main");
		return mv;
	}
	
	//�α��� ó��
	@ResponseBody
	@RequestMapping(value = "/login.mc", method = RequestMethod.POST)
	public boolean loginimpl(String inputId, String inputPwd,HttpSession session) throws Exception {
		
		boolean msg=false;
		 
		UserVO dbUser = uservice.get(inputId);

		//Database �� �Է¹��� id �� �ش��ϴ� User �� �ִٸ�
		if(dbUser!=null) {
			if (dbUser.getPwd().equals(inputPwd)) { //id �� �ش��ϴ� Database �� ��й�ȣ�� �Է��� ��й�ȣ�� ��ġ�Ѵٸ�
				
				//session �� id �� id �� �ش��ϴ� ������� �̸��� �����Ѵ�.
				session.setAttribute("loginId", inputId);
				session.setAttribute("userName", dbUser.getUname());
				msg = true;
			}
		}
		return msg;
	}
	
	//�α׾ƿ�
	@RequestMapping("/logout.mc")
	public String logout(HttpSession session) {
		ModelAndView mv = new ModelAndView();
		if (session != null) {
			session.invalidate();
		}
		return "redirect:main.mc";
	}
}





