package com.neu.yelp.controller;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.LoggingMXBean;

import com.neu.yelp.dao.BusinessDAO;
import com.neu.yelp.dao.UserDAO;
import com.neu.yelp.pojo.BusinessLatLong;
import com.neu.yelp.pojo.User;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value="/" , method= RequestMethod.GET)
	public String home(Model model){		
		model.addAttribute("loggedInUser", new User());
		return "login";
		
	}
	
	
	@RequestMapping(value = "/login.htm", method = RequestMethod.POST)
	public ModelAndView doSubmitAction(@ModelAttribute("loggedInUser") User loggedInUser,  HttpSession session) {
		ModelAndView mv = new ModelAndView();
		
		UserDAO userDao = new UserDAO();
		String userid = userDao.authenticateUser(loggedInUser.getUsername(), loggedInUser.getPassword());
		loggedInUser.setUserid(userid);
		List<BusinessLatLong> latList = new ArrayList<BusinessLatLong>();
		List<BusinessLatLong> latListNotReviewed = new ArrayList<BusinessLatLong>();

		BusinessDAO businessDAO = new BusinessDAO();
		if(!userid.isEmpty()){
			
			latList = businessDAO.getUserBusinessLatLong(loggedInUser);
			latListNotReviewed = businessDAO.getUserNewBusinessLatLong2(loggedInUser);
			
		}
		
		mv.addObject("LatLongList", latList);
		mv.addObject("LatLongListNotReviewed", latListNotReviewed);
		mv.addObject("country_latitude",businessDAO.getStateLatitutde(loggedInUser.getHotState()));
		mv.addObject("country_longitude",businessDAO.getStateLongitude(loggedInUser.getHotState()));
		mv.addObject("user",loggedInUser);
	
		
		mv.setViewName("index");
		return mv;
	}
	
}
