<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<%
	String path = request.getContextPath() ;
%>
<head>
	<meta charset="UTF-8">
	<title>登录</title>
	<style>
		*{margin:0;padding:0;border:0;}
		html,body{
			height:100%;
			min-height:662px;
			min-width: 1024px;
		}
		.wrap{
			background:url(img/login_bg.jpg) no-repeat 100%;
			height:100%;
			position: relative;
		}
		.login{
			position: absolute;
			width:35%;
			height:30%;
			top:0;left:0;right:0;bottom:0;margin:auto;
			color:rgb(0,126,236);
		}
		.login h1{
			height:10%;
			padding-bottom:10%;
			text-align:center;
		}
		.login>div{
			width: 70%;
			background:rgba(0,0,0,0.6);
			margin:0 auto 5%;
			height:18%;
			display: flex;
			align-items:center;
		}
		.login>div.inputs img{
			height:60%;
			margin:2%;
		}
		.login>div.inputs input{
			background:none;
			color:#FFF;
			height: 100%;
			border: 0px;outline:none;
		}
		.login>div.inputs input::-webkit-input-placeholder {
	         color: #FFF;
	         font-size: 14px;
	         letter-spacing: 3px;
	     }
		.login .btnLogin{
			cursor:pointer;
			border-radius: 5px;
			background:rgb(0,126,236);
			color:#FFF;
		}
		.login .btnLogin span{
			margin:0 auto;
			letter-spacing: 5px;
		}
		.login .btnLogin span:hover{
			font-weight: bold;
		}
	</style>
</head>
<body>
	<%--<div class="wrap">--%>
		<%--<div class="login">--%>
			<%--<h1>model</h1>--%>
			<%--<div class="inputs">--%>
				<%--<img src="img/user.png" alt=""><input id="userName" name="userName" type="text" placeholder="账号">--%>
			<%--</div>--%>
			<%--<div class="inputs">--%>
				<%--<img src="img/psw.png" alt=""><input id="password" name="password" type="text" placeholder="密码">--%>
			<%--</div>--%>
			<%--<div class="btnLogin" name="button" type="submit" id="submit"><span>登录</span></div>--%>
		<%--</div>--%>
	<%--</div>--%>

	${msg }
	<br>
	<form action="${pageContext.request.contextPath}/upload" method="post" enctype="multipart/form-data">
		<input type="file" name="file">
		<input type="submit" value="提交">
	</form>


	<script src='${pageContext.request.contextPath}/js/jquery.min.js' type='text/javascript'></script>
	<script type='text/javascript'>
	$(document).ready(function(){

	    console.log('${pageContext.request.contextPath}')



		$("#userName").keydown(function(e){
			if(e.which == 13){
				$("#submit").click();
			}
		});
		
		$("#password").keydown(function(e) {
			if (e.which == 13) {
				$("#submit").click();
			}
		});
		
		$("#submit").click(function() {
			var account = $("#userName").val();
			if(account == ''){
				alert("账号必须填写!!");
				return;
			}
			if($("#password").val() == ''){
				alert("密码必须填写！！");
				return;
			}
			$.ajax({
				url:"${pageContext.request.contextPath}/validateLogin",
				type:"POST",
				data:{account:$("#userName").val(),password:$("#password").val()},
				success:function(data){
					if(data == 'success'){
						location.href = '${pageContext.request.contextPath}/index';
					}else{
						alert("账号或密码错误");
					}
				},
				error:function(){
					alert("登录失败");
				}
			
			});
		})
		
	});
</script>
</body>
</html>
