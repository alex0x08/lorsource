<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8"%>
<%--
  ~ Copyright 1998-2022 Linux.org.ru
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~        http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  --%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="lor" %>
<%--@elvariable id="template" type="ru.org.linux.site.Template"--%>
<%--@elvariable id="bonus" type="java.lang.Boolean"--%>
<%--@elvariable id="msgid" type="java.lang.Integer"--%>
<%--@elvariable id="author" type="ru.org.linux.user.User"--%>
<%--@elvariable id="draft" type="java.lang.Boolean"--%>
<%--@elvariable id="uncommited" type="java.lang.Boolean"--%>

<jsp:include page="/WEB-INF/jsp/head.jsp"/>

<title>Отметить найденное решение</title>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<h1>Отметить найденное решение</h1>
<c:if test="${draft}">
    Нельзя добавлять решение для неподтверждённых тем!
</c:if>
<form method=POST action="solve.jsp" class="form-horizontal">
<lor:csrf/>
  <div class="control-group">
    <label class="control-label" for="reason-input">
      Заметки
    </label>

    <div class="controls">
      <input type=text name="remark"/>
    </div>
  </div>

  <c:if test="${template.moderatorSession and bonus}">
  <div class="control-group">
    <label class="control-label" for="bonus-input">
      Штраф<br>
      score автора: ${author.score}
    </label>
    <div class="controls">
      <input id="bonus-input" type=number name=bonus value="7" min="0" max="20">
      <span class="help-inline">(от 0 до 20)</span>
    </div>
  </div>
  </c:if>

  <input type="hidden" name="msgid" value="${msgid}">
  <input type="hidden" name="solutionid" value="${solutionid}">

  <div class="control-group">
    <div class="controls">
      <button type=submit class="btn btn-primary">Отметить</button>
    </div>
  </div>
</form>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
