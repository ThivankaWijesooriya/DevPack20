package com.ONEzero.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ONEzero.DAO.AuthoritiesDAO;
import com.ONEzero.DAO.AuthoritiesDAOImpl;
import com.ONEzero.DAO.EmployeeDAO;
import com.ONEzero.DAO.EmployeeDAOImpl;
import com.ONEzero.DAO.IouDAO;
import com.ONEzero.DAO.IouDAOImpl;
import com.ONEzero.DAO.VoucherDAO;
import com.ONEzero.DAO.VoucherDAOImpl;
import com.ONEzero.model.Employee;
import com.ONEzero.model.Iou;
import com.ONEzero.model.Pages;
import com.ONEzero.model.Voucher;

@Controller
public class VoucherController {

	private Logger logger = Logger.getLogger(VoucherController.class);
	private HttpSession session;

	@InitBinder
	public void initBinder(WebDataBinder databinder) {

		StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
		databinder.registerCustomEditor(String.class, trimmerEditor);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		databinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));

	}

	@RequestMapping(value = "/Voucher", method = RequestMethod.GET)
	public String ViewVoucher(Model model,HttpServletRequest request) {

		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
		   return "redirect:/login";
					
		}
		
		else {
			
			VoucherDAO voudao = new VoucherDAOImpl();
    		
			IouDAO ioudao = new IouDAOImpl();
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);
			
			String userid = (String) session.getAttribute("userid");
			
			String voulist = voudao.getAllVoucherByUserId(userid);

			model.addAttribute("voulist", voulist);

			return "pVoucher";
			
		}


	}

	@RequestMapping(value = "/Voucher/Create", method = RequestMethod.GET)
	public String CreateVoucher(Model model,HttpServletRequest request) {

		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			IouDAO ioudao = new IouDAOImpl();
			EmployeeDAO retreobj = new EmployeeDAOImpl();
			

			Map<String, String> CatType = ioudao.fillColumnCategoryType();
			String userid = (String) session.getAttribute("userid");
			Map ioucolumn = ioudao.fillColumnIOU(userid);
			String balance = ioudao.getPettyCashBalance();		
			Employee emp = retreobj.getEmployeeById(userid);
			

			model.addAttribute("voucher", new Voucher());
			model.addAttribute("ioucolumn", ioucolumn);
			model.addAttribute("CatListType", CatType);
			model.addAttribute("balance",balance);		
			model.addAttribute("rptoff", emp.getTemprptoff1());
			

			return "pCreateVoucher";
			
		}
		

	}

	@RequestMapping(value = "/Voucher/Create/Submit", method = RequestMethod.POST)
	public String SubmitVoucher(@Valid @ModelAttribute("voucher") Voucher voucher, BindingResult br, Model model,
			HttpServletRequest request) {

		IouDAO ioudao = new IouDAOImpl();
		VoucherDAO voucherdao = new VoucherDAOImpl();
		Employee emp = new Employee();
		session = request.getSession();
		EmployeeDAO retreobj = new EmployeeDAOImpl();
		

		Map<String, String> CatType = ioudao.fillColumnCategoryType();
		String userid = (String) session.getAttribute("userid");
		Map ioucolumn = ioudao.fillColumnIOU(userid);
		String balance = ioudao.getPettyCashBalance();
		emp = retreobj.getEmployeeById(userid);
		String createdBy = (String) session.getAttribute("name");
		

		
		model.addAttribute("ioucolumn", ioucolumn);
		model.addAttribute("CatListType", CatType);		
		model.addAttribute("balance",balance);		
		model.addAttribute("rptoff", emp.getTemprptoff1());
		
		

		if (br.hasErrors()) {

			logger.info("Errors Have Occured");

			return "pCreateVoucher";

		}

		else {
			
			
			voucher.setEnteredby(createdBy);
			
			emp.setUserid(userid);
			voucher.setEmployee(emp);

			voucherdao.createVoucher(voucher);
			// Create Voucher In DB

			return "redirect:/Voucher";

		}

	}

	@RequestMapping(value = "/Voucher/Update", method = RequestMethod.GET)
	public String UpdateVoucher(@RequestParam("vouid") String vouid, Model model, HttpServletRequest request) {

		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			IouDAO ioudao = new IouDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			EmployeeDAO retreobj = new EmployeeDAOImpl();
			

			Map<String, String> CatType = ioudao.fillColumnCategoryType();
			String userid = (String) session.getAttribute("userid");
			Map ioucolumn = ioudao.fillColumnIOU(userid);
			Voucher vou = voudao.getVoucherById(vouid);
			String balance = ioudao.getPettyCashBalance();
			Employee emp = retreobj.getEmployeeById(userid);
			

			model.addAttribute("ioucolumn", ioucolumn);
			model.addAttribute("CatListType", CatType);
			model.addAttribute("voucher", vou);		
			model.addAttribute("balance",balance);		
			model.addAttribute("rptoff", emp.getTemprptoff1());
			

			return "pUpdateVoucher";
			
		}
		

	}

	@RequestMapping(value = "/Voucher/Update/Submit", method = RequestMethod.POST)
	public String UpdateVoucherSubmit(@Valid @ModelAttribute("voucher") Voucher voucher, BindingResult br, Model model,
	HttpServletRequest request) {
		
		
		IouDAO ioudao = new IouDAOImpl();
		VoucherDAO voucherdao = new VoucherDAOImpl();
		Employee emp = new Employee();
		session = request.getSession();
		EmployeeDAO retreobj = new EmployeeDAOImpl();
		
				
		Map<String, String> CatType = ioudao.fillColumnCategoryType();
		String userid = (String) session.getAttribute("userid");
		Map ioucolumn = ioudao.fillColumnIOU(userid);
		String balance = ioudao.getPettyCashBalance();
		emp = retreobj.getEmployeeById(userid);
		String createdBy = (String) session.getAttribute("name");
		

		model.addAttribute("ioucolumn", ioucolumn);
		model.addAttribute("CatListType", CatType);		
		model.addAttribute("balance",balance);		
		model.addAttribute("rptoff", emp.getTemprptoff1());
		
				
		
		if(br.hasErrors()) {
			
			logger.info("Error has Occured");
			return "pUpdateVoucher";
		}
		
		else {
			
									
			voucher.setUpdatedby(createdBy);
			
			emp.setUserid(userid);
			voucher.setEmployee(emp);
						
			voucherdao.updateVoucherById(voucher);
			
			return "redirect:/Voucher";
			
		}

		

	}

	@RequestMapping(value = "/Voucher/Delete", method = RequestMethod.GET)
	public String DeleteVoucher(@RequestParam("vouid2") String vouid2, Model model,HttpServletRequest request) {

		
		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			IouDAO ioudao = new IouDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			EmployeeDAO retreobj = new EmployeeDAOImpl();
			

			Map<String, String> CatType = ioudao.fillColumnCategoryType();
			String userid = (String) session.getAttribute("userid");
			Map ioucolumn = ioudao.fillColumnIOU(userid);
			Voucher vou = voudao.getVoucherById(vouid2);
			String balance = ioudao.getPettyCashBalance();
			Employee emp = retreobj.getEmployeeById(userid);
			
			
			model.addAttribute("ioucolumn", ioucolumn);
			model.addAttribute("CatListType", CatType);
			model.addAttribute("voucher", vou);		
			model.addAttribute("balance",balance);		
			model.addAttribute("rptoff", emp.getTemprptoff1());
			
			
			return "pDeleteVoucher";
			
		}
		

	}

	@RequestMapping(value = "/Voucher/Delete/Submit", method = RequestMethod.POST)
	public String DeleteVoucherSubmit(@Valid @ModelAttribute("voucher") Voucher voucher, BindingResult br, Model model,
			HttpServletRequest request) {
		
		IouDAO ioudao = new IouDAOImpl();
		VoucherDAO voucherdao = new VoucherDAOImpl();
		Employee emp = new Employee();
		session = request.getSession();
		EmployeeDAO retreobj = new EmployeeDAOImpl();
		
				
		Map<String, String> CatType = ioudao.fillColumnCategoryType();
		String userid = (String) session.getAttribute("userid");
		Map ioucolumn = ioudao.fillColumnIOU(userid);
		String balance = ioudao.getPettyCashBalance();
		emp = retreobj.getEmployeeById(userid);
		String createdBy = (String) session.getAttribute("name");
		

		model.addAttribute("ioucolumn", ioucolumn);
		model.addAttribute("CatListType", CatType);
		model.addAttribute("balance",balance);		
		model.addAttribute("rptoff", emp.getTemprptoff1());
		
		
		
		if(br.hasErrors()) {
			
			logger.info("Has Errors");
			
			return "pDeleteVoucher";
		}
		
		else {
			
           			
			voucher.setDeletedby(createdBy);
			
			emp.setUserid(userid);
			
			voucher.setEmployee(emp);
						
			voucherdao.deleteVoucherById(voucher);
			
			
			return "redirect:/Voucher";
			
		}
		

	}

	

	@RequestMapping(value = "/Voucher/Approve/Readonly", method = RequestMethod.GET)
	public String viewApproveVoucher(@RequestParam("vouid1") String vouid, Model model, HttpServletRequest request) {

		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			
			IouDAO ioudao = new IouDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			AuthoritiesDAO authority = new AuthoritiesDAOImpl();
			EmployeeDAO retreobj = new EmployeeDAOImpl();
			

			String userid = (String) session.getAttribute("userid");
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);
					
			
			if(authority.AuthorizedReportingOfficer(userid)==true) {
				
				
				Voucher voucher = voudao.getVoucherById(vouid);		
				Map<String, String> CatType =ioudao.fillColumnCategoryType();
				/*Map ioucolumn = ioudao.fillColumnIOU();*/
				Map ioucolumn = ioudao.fillColumnIouWithReturns();
				
				Employee emp = retreobj.getEmployeeById(userid);
				Boolean AuthReject = authority.ValidateReject(vouid);
				
				
				model.addAttribute("voucher", voucher);
				model.addAttribute("CatListType",CatType);
				model.addAttribute("ioucolumn", ioucolumn);						
				model.addAttribute("rptoff", emp.getTemprptoff1());
				model.addAttribute("AuthReject",AuthReject);
				model.addAttribute("AppComments",voucher.getAppcomments());
				
				
				return "pViewVoucherApproval";
							
				
			}
			
			else {
				
				
				return "redirect:/AccessDenied";
				
			}
			
		}
		
				

	}

	@RequestMapping(value = "/Voucher/Approve/Submit", method = RequestMethod.POST)
	public String viewApproveVoucherSubmit(@Valid @ModelAttribute("voucher") Voucher voucher, BindingResult br, Model model,
			@RequestParam("comments") String comments, HttpServletRequest request) {
		

		IouDAO ioudao = new IouDAOImpl();
		VoucherDAO voucherdao = new VoucherDAOImpl();
		Employee emp = new Employee();
		session = request.getSession();
		EmployeeDAO retreobj = new EmployeeDAOImpl();
		AuthoritiesDAO authority = new AuthoritiesDAOImpl();
		
				
		Map<String, String> CatType = ioudao.fillColumnCategoryType();
		/*Map ioucolumn = ioudao.fillColumnIOU();*/
		Map ioucolumn = ioudao.fillColumnIouWithReturns();
		String balance = ioudao.getPettyCashBalance();
		String userid = (String) session.getAttribute("userid");
		emp = retreobj.getEmployeeById(userid);
		String approvedBy = (String) session.getAttribute("name");
		Boolean AuthReject = authority.ValidateReject(voucher.getVoucherno());
		

		model.addAttribute("ioucolumn", ioucolumn);
		model.addAttribute("CatListType", CatType);			
		model.addAttribute("balance",balance);
		model.addAttribute("rptoff", emp.getTemprptoff1());
		model.addAttribute("AuthReject",AuthReject);
		
		
	
     if(br.hasErrors()) {
    	 
    	 
    	 return "pViewVoucherApproval";
     }
     
     else {
    	 
    	    		    
    	    voucher.setComments(comments);
		      	    
		   
		    voucher.setApprovedby(approvedBy);
		    
		    
		    voucherdao.ApproveVoucherById(voucher);
		    
    	 
    	    return "redirect:/Request/Approve/GET";
    	 
     }
		

	}

	@RequestMapping(value = "/Voucher/Reject/Submit", method = RequestMethod.POST)
	public String viewRejectVoucherSubmit(@Valid @ModelAttribute("voucher") Voucher voucher, BindingResult br, Model model,
			@RequestParam("comments") String comments, HttpServletRequest request,
			@RequestParam("actionstat") String actionstat) {

	
		IouDAO ioudao = new IouDAOImpl();
		VoucherDAO voucherdao = new VoucherDAOImpl();
		Employee emp = new Employee();
		session = request.getSession();
		EmployeeDAO retreobj = new EmployeeDAOImpl();
		
				
		Map<String, String> CatType = ioudao.fillColumnCategoryType();
		/*Map ioucolumn = ioudao.fillColumnIOU();*/
		Map ioucolumn = ioudao.fillColumnIouWithReturns();
		String balance = ioudao.getPettyCashBalance();
		String userid = (String) session.getAttribute("userid");
		emp = retreobj.getEmployeeById(userid);
		String rejectedBy = (String) session.getAttribute("name");
		

		model.addAttribute("ioucolumn", ioucolumn);
		model.addAttribute("CatListType", CatType);		
		model.addAttribute("balance",balance);		
		model.addAttribute("rptoff", emp.getTemprptoff1());
		
		
		
		if(br.hasErrors()) {
			
			
			return null;
			
		}
		
		else {
			
			    		    
			    voucher.setComments(comments);
			    			   
			    voucher.setApprovedby(rejectedBy);
			    
			    voucherdao.RejectVoucherById(voucher, actionstat);
			    
			    if(actionstat.equalsIgnoreCase("AppRejectVOU")) {
			    	
			    	return "redirect:/Request/Approve/GET";
			    	
			    }
			    
			    if(actionstat.equalsIgnoreCase("AuthRejectVOU")) {
			    	
			    	
			    	return "redirect:/Request/Authorize/GET";
			    	
			    }
			   
			    return null;
			
		}

		

	}
	
	
	@RequestMapping(value = "/Request/Approve/GET", method = RequestMethod.GET)
	public String ApproveVoucher(Model model, HttpServletRequest request) {
		
		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			
			AuthoritiesDAO authority = new AuthoritiesDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			
			IouDAO ioudao = new IouDAOImpl();
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);
			

			String userid = (String) session.getAttribute("userid");
			
			
			
			if(authority.AuthorizedReportingOfficer(userid)==true) {
				
				
				String voulist = voudao.getAllTransactionByRptOfficer(userid);

				model.addAttribute("voulist", voulist);
				
				return "pApprove";
										
			}
			
			else {
				
				
				return "redirect:/AccessDenied";
				
			}
			
		}
						

	}

	@RequestMapping(value = "/Request/Authorize/GET", method = RequestMethod.GET)
	public String AuthorizeVoucher(Model model, HttpServletRequest request) {
		
		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			
			int pageid = 5;
			AuthoritiesDAO authdao = new AuthoritiesDAOImpl();
			IouDAO ioudao = new IouDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			
			
			String userid = (String) session.getAttribute("userid");
			
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);
			
					
			List<Pages> AccessiblePages = authdao.AuthorizedPageViewer(userid);
			
			
			 for(Pages page : AccessiblePages) {

				  if(page.getPageid()== pageid) {
					  
					  
						String iouList = ioudao.getallTransactionIOUandReturns();
						String vouList = voudao.getAllTransactionVoucher();

						model.addAttribute("Iou", iouList);
						model.addAttribute("Vou",vouList);

						return "pAuthorize";
					  
					  
				  }
				  
				  
	        }
			
			 return "redirect:/AccessDenied";
			
		}
		
		

	}

	@RequestMapping(value = "/Voucher/Authorize/Readonly", method = RequestMethod.GET)
	public String viewAuthorizeVoucher(@RequestParam("vouid") String vouid, Model model, HttpServletRequest request) {
					
		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			
			int pageid = 5;
			
			IouDAO ioudao = new IouDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			
			AuthoritiesDAO authdao = new AuthoritiesDAOImpl();
			EmployeeDAO retreobj = new EmployeeDAOImpl();
			
				
			String userid = (String) session.getAttribute("userid");
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);
			
					
			List<Pages> AccessiblePages = authdao.AuthorizedPageViewer(userid);
			
			 for(Pages page : AccessiblePages) {

				  if(page.getPageid()== pageid) {
					  
					  
						Voucher voucher = voudao.getVoucherById(vouid);		
						Map<String, String> CatType =ioudao.fillColumnCategoryType();
						/*Map ioucolumn = ioudao.fillColumnIOU();*/
						Map ioucolumn = ioudao.fillColumnIouWithReturns();
						
						Employee emp = retreobj.getEmployeeById(userid);
						Boolean AuthReject = authdao.ValidateReject(vouid);
						Boolean AuthApproved = authdao.ValidateApproved(vouid);
						
						
						model.addAttribute("voucher", voucher);
						model.addAttribute("CatListType",CatType);
						model.addAttribute("ioucolumn", ioucolumn);					
											
						model.addAttribute("rptoff", emp.getTemprptoff1());
						model.addAttribute("AuthReject",AuthReject);
						model.addAttribute("AuthApproved",AuthApproved);
						model.addAttribute("authcomments",voucher.getAuthcomments());
						

						return "pViewVoucherAuthorize";
					   				  				  
				  }
				  			  
	         }
			
			 return "redirect:/AccessDenied";
			
		}
		
		
	}

	@RequestMapping(value = "/Voucher/Authorize/Submit", method = RequestMethod.POST)
	public String viewAuthorizeVoucherSubmit(@Valid @ModelAttribute("voucher") Voucher voucher, BindingResult br, Model model,
	@RequestParam("comments") String comments, HttpServletRequest request) {
		
		session = request.getSession();
		IouDAO ioudao = new IouDAOImpl();
		VoucherDAO voucherdao = new VoucherDAOImpl();
		Employee emp = new Employee();
		EmployeeDAO retreobj = new EmployeeDAOImpl();
		AuthoritiesDAO authdao = new AuthoritiesDAOImpl();
		
				
		Map<String, String> CatType = ioudao.fillColumnCategoryType();
		/*Map ioucolumn = ioudao.fillColumnIOU();*/
		Map ioucolumn = ioudao.fillColumnIouWithReturns();
		String balance = ioudao.getPettyCashBalance();
		String userid = (String) session.getAttribute("userid");
		emp = retreobj.getEmployeeById(userid);
		String authorizedBy = (String) session.getAttribute("name");
		Boolean AuthReject = authdao.ValidateReject(voucher.getVoucherno());
		Boolean AuthApproved = authdao.ValidateApproved(voucher.getVoucherno());
		

		model.addAttribute("ioucolumn", ioucolumn);
		model.addAttribute("CatListType", CatType);			
		model.addAttribute("balance",balance);		
		model.addAttribute("rptoff", emp.getTemprptoff1());
		model.addAttribute("AuthReject",AuthReject);
		model.addAttribute("AuthApproved",AuthApproved);
		
		
		if(br.hasErrors()) {
			
			
			return "pViewVoucherAuthorize";
			
		}
	
	    else {
		
		    		    
		    voucher.setComments(comments);
		    		    		   
		    voucher.setAuthorizedby(authorizedBy);
				    
		    voucherdao.AuthorizeVoucherById(voucher);
		
		    return "redirect:/Request/Authorize/GET";
		
	    }

	}
	
	@RequestMapping(value="/Voucher/GetVoucherInquiry", method = RequestMethod.GET)
	public String getAuthorizedVouchers(Model model,HttpServletRequest request) {
		
		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			VoucherDAO voudao = new VoucherDAOImpl();
			
			String userid = (String)session.getAttribute("userid");
			String voulist = voudao.getAllAuthorizedVouchers(userid);
			
			IouDAO ioudao = new IouDAOImpl();
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);

			model.addAttribute("voulist", voulist);
			
			return "pVoucherInquiry";
			
		}

	}
	
	
	@RequestMapping(value = "/Voucher/Inquire/Readonly", method = RequestMethod.GET)
	public String viewInquireVoucher(@RequestParam("vouid1") String vouid, Model model, HttpServletRequest request) {

		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			
		    IouDAO ioudao = new IouDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			
			AuthoritiesDAO authority = new AuthoritiesDAOImpl();
			EmployeeDAO retreobj = new EmployeeDAOImpl();
		

		    String userid = (String) session.getAttribute("userid");
							
			
			Voucher voucher = voudao.getVoucherById(vouid);		
			Map<String, String> CatType =ioudao.fillColumnCategoryType();
			Map ioucolumn = ioudao.fillColumnIOU(userid);
			String balance = ioudao.getPettyCashBalance();
			Employee emp = retreobj.getEmployeeById(userid);
			Boolean AuthReject = authority.ValidateReject(vouid);
			
			
			model.addAttribute("voucher", voucher);
			model.addAttribute("CatListType",CatType);
			model.addAttribute("ioucolumn", ioucolumn);			
			model.addAttribute("balance",balance);			
			model.addAttribute("rptoff", emp.getTemprptoff1());
			model.addAttribute("AuthReject",AuthReject);
			
			
			return "pViewVoucherInquiry";
			
		}
		

	}
	
	@RequestMapping(value="/Request/Authorize/Inquiry",method=RequestMethod.GET)
	public String getAuthorizedTransactions(Model model,HttpServletRequest request) {
		
		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			VoucherDAO voudao = new VoucherDAOImpl();
			String transList = voudao.getAllTransaction();
			
			IouDAO ioudao = new IouDAOImpl();
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);
			
			model.addAttribute("voulist",transList);
			
			return "pInquiryAuthorized";
			
		}
		

	}
	
	
	@RequestMapping(value = "/Inquiry/Voucher/Authorize/Readonly", method = RequestMethod.GET)
	public String inquiryViewAuthorizeVoucher(@RequestParam("vouid") String vouid, Model model, HttpServletRequest request) {
					
		session = request.getSession();
		
		if(session.getAttribute("LoggedName")== null) {
				
			return "redirect:/login";
					
		}
		
		else {
			
			
			int pageid = 5;
			IouDAO ioudao = new IouDAOImpl();
			VoucherDAO voudao = new VoucherDAOImpl();
			
			AuthoritiesDAO authdao = new AuthoritiesDAOImpl();
			EmployeeDAO retreobj = new EmployeeDAOImpl();
			
				
			String userid = (String) session.getAttribute("userid");
			String balance = ioudao.getPettyCashBalance();
			model.addAttribute("balance",balance);	
			
					
			List<Pages> AccessiblePages = authdao.AuthorizedPageViewer(userid);
			
			 for(Pages page : AccessiblePages) {

				  if(page.getPageid()== pageid) {
					  
					  
						Voucher voucher = voudao.getVoucherById(vouid);		
						Map<String, String> CatType =ioudao.fillColumnCategoryType();
						/*Map ioucolumn = ioudao.fillColumnIOU();*/
						Map ioucolumn = ioudao.fillColumnIouWithReturns();
						
						Employee emp = retreobj.getEmployeeById(userid);
						Boolean AuthReject = authdao.ValidateReject(vouid);
						
						
						model.addAttribute("voucher", voucher);
						model.addAttribute("CatListType",CatType);
						model.addAttribute("ioucolumn", ioucolumn);					
										
						model.addAttribute("rptoff", emp.getTemprptoff1());
						model.addAttribute("AuthReject",AuthReject);
						model.addAttribute("authcomments",voucher.getAuthcomments());
						

						return "pInquiryViewVoucherAuthorize";
					   				  				  
				  }
				  			  
	         }
			
			 return "redirect:/AccessDenied";
			
		}
		
	}
	
	
	@RequestMapping(value="/Request/Authorize/Inquiry/Bystatus", method=RequestMethod.GET)
	@ResponseStatus(value=HttpStatus.OK)
	public @ResponseBody String getTransactionByStatus(@RequestParam(value="status",required=false, defaultValue="")String status) {
		
		VoucherDAO voucherdao = new VoucherDAOImpl();
		String list = voucherdao.getAllInquiryByStatus(status);
		return list;
	}
	
	@RequestMapping(value="/Request/Authorize/Inquiry/Search", method=RequestMethod.GET)
	@ResponseStatus(value=HttpStatus.OK)
	public @ResponseBody String getTransactionBySearch(@RequestParam(value="fromDate",required=false,defaultValue="")String fromDate,
	@RequestParam(value="toDate",required=false,defaultValue="")String toDate, @RequestParam(value="status",required=false,defaultValue="")String status) 
	{
		
		
		VoucherDAO voucherdao = new VoucherDAOImpl();
		String inquiryList = voucherdao.getAllInquiryBySearch(fromDate, toDate, status);
		return inquiryList;
		
	}

}
