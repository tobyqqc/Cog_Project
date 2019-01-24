package com.lumosity.question;

import com.jfinal.core.Controller;
import com.lumosity.model.Account;
import com.lumosity.model.QuestionStatus;

public class QuestionController extends Controller {

	public void index(){
		render("/question/question.html");
	}
	
	/**
	 * 表单的保存
	 */
	public void save(){
		
		Account account = getSessionAttr("userInfo");
		Long userId = account.getUserId();
		account.set("gender", getParaToInt("gender")).set("educationId", getParaToInt("education_level"))
				.set("jobId", getParaToInt("occupation")).update();
		
		QuestionStatus status = QuestionStatus.dao.findById(userId);
		if (status != null) {
			status.set("question1Status", 1).update();
		} else {
			new QuestionStatus().set("userId", userId).set("question1Status", 1).save();
		}
		
		redirect("/question/secondQuestion");
	}
	
	public void secondQuestion() {
		render("/question/questionnaire.html");
	}
	
	public void secondSave(){
		Account account = getSessionAttr("userInfo");
		Long userId = account.getUserId();
		StringBuffer survey = new StringBuffer("");
		
		for (int i = 5; i < 10; i++) {
			String[] qvalues = this.getParaValues("q"+i);
			survey.append("question").append(i-4).append("_result:").append(String.join(",", qvalues));
			if (i != 9) {
				survey.append(";");
			}
		}
		
		QuestionStatus status = QuestionStatus.dao.findById(userId);
		if (status != null) {
			status.set("question2Status", 1).set("survey", survey.toString()).update();
		} else {
			new QuestionStatus().set("userId", userId).set("question2Status", 1).save();
		}
		redirect("/fitTest");
	}

	/**
	 * 跳过问卷
	 */
	public void skipQuestion(){
		//设置回话session
		setSessionAttr("questionSkip",true);
		redirect("/home");
	}
}
