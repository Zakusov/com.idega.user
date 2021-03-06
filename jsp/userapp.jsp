<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
        xmlns:h="http://java.sun.com/jsf/html"
        xmlns:jsf="http://java.sun.com/jsf/core"
        xmlns:wf="http://xmlns.idega.com/com.idega.webface"
        xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
 		xmlns:c="http://xmlns.idega.com/com.idega.content"
        xmlns:x="http://myfaces.apache.org/tomahawk"
        xmlns:web2="http://xmlns.idega.com/com.idega.block.web2.0"
        xmlns:f="http://java.sun.com/jsf/core" 
        xmlns:builder="http://xmlns.idega.com/com.idega.builder"
                
version="1.2">
	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<jsf:view>
		<ws:page id="pages" javascripturls="
						/dwr/engine.js,
        				/dwr/interface/BuilderService.js,
        				/idegaweb/bundles/com.idega.content.bundle/resources/javascript/drag-drop-folder-tree.js,
						/idegaweb/bundles/com.idega.content.bundle/resources/javascript/tree.js">
						
			<h:form id="userAppForm">
                		<builder:module id="userToobar" componentClass="com.idega.user.app.UserApplication" />       	  
           </h:form>
		</ws:page>
	</jsf:view>
</jsp:root>