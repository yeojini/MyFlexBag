package com.controller;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.frame.Service;
import com.vo.ItemVO;
import com.vo.PageVO;
import com.vo.ReviewVO;

@Controller
public class ItemController {

	@Resource(name="iservice")
	Service<String, ItemVO> service;
	
	@Resource(name="rservice")
	Service<String, ReviewVO> rservice;
	
	@Resource(name="pservice")
	Service<String, PageVO> pservice;
	
	@RequestMapping("/iadd.mc")
	public ModelAndView add(HttpSession session, ModelAndView mv) {
		String id = (String) session.getAttribute("loginid");
		if(!id.equals("admin")) {
			mv.setViewName("main");
			return mv;
		}

		mv.addObject("center", "item/add");
		mv.setViewName("main");
		return mv;
	}
	
	@RequestMapping("/iaddimpl.mc")
	public String addimpl(ItemVO item) {
		
		if(item.getMf1()!=null ) { item.setImg1(item.getMf1().getOriginalFilename()); }
		if(item.getMf2()!=null ) { item.setImg2(item.getMf2().getOriginalFilename()); }
		if(item.getMf3()!=null ) { item.setImg3(item.getMf3().getOriginalFilename()); }
		if(item.getMf4()!=null ) { item.setImg4(item.getMf4().getOriginalFilename()); }
		if(item.getMf5()!=null ) { item.setImg5(item.getMf5().getOriginalFilename()); }
		
		try {
			service.register(item);
			Util.saveFile(item.getMf1());
			Util.saveFile(item.getMf2());
			Util.saveFile(item.getMf3());
			Util.saveFile(item.getMf4());
			Util.saveFile(item.getMf5());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:ilist.mc";
	}
	
	@RequestMapping("/idel.mc")
	public String delete(HttpSession session, String pid) {
		String id = (String) session.getAttribute("loginid");
		if(!id.equals("admin")) {
			return "redirect:ilist.mc";
		}
		try {
			service.remove(pid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:ilist.mc";
	}
	@RequestMapping("/iupdateimpl.mc")
	public String updateimpl(ItemVO item) {
		try {
			service.modify(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int pid = item.getPid();
		return "redirect:idetail.mc?pid="+pid;
	}
	
	@RequestMapping("/iupdate.mc")
	public ModelAndView update(ModelAndView mv,	String pid, HttpSession session) {
		String id = (String) session.getAttribute("loginid");
		if(!id.equals("admin")) {
			mv.setViewName("ilist.mc");
			return mv;
		}
		ItemVO item = null;
		try {
			item = service.get(pid);
			mv.addObject("ditem", item);
			mv.addObject("center", "item/update");
			mv.setViewName("main");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	@RequestMapping("/idetail.mc")
	public ModelAndView detail(ModelAndView mv, ItemVO item, PageVO pagevo, ReviewVO rv) {
		ArrayList<ReviewVO> reviewList;
		try {
			item = service.get(""+item.getPid());
			reviewList = rservice.getall(rv);
			
			mv.addObject("pagevo",pagevo);
			mv.addObject("pagelink",pagevo.getListLink());
			mv.addObject("ditem", item);
			mv.addObject("reviewList", reviewList);
			mv.addObject("center", "item/detail");
			mv.setViewName("main");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	@RequestMapping("/ilist.mc")
	public ModelAndView list(ModelAndView mv, ItemVO vo, PageVO pageprev) { //String cateid,

		ArrayList<ItemVO> list = null;
		ArrayList<PageVO> plist = null;
		try {
			mv.addObject("pageprev",pageprev);
			pageprev.setTablename("Lproduct");

			list = service.getall(vo);
			plist = pservice.getall(pageprev);
			
			PageVO pagenext = plist.get(0);
			pagenext.setPage(pageprev.getPage());
			pagenext.calcData(pagenext.getPage());

			mv.addObject("pagenext", pagenext);	
			mv.addObject("base", "ilist.mc");
			mv.addObject("ilist", list);
			mv.addObject("center", "item/list");
			mv.setViewName("main");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
}






