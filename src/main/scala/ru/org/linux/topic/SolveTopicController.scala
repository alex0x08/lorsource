package ru.org.linux.topic


import com.typesafe.scalalogging.StrictLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam}
import org.springframework.web.servlet.ModelAndView
import ru.org.linux.auth.AccessViolationException
import ru.org.linux.auth.AuthUtil.AuthorizedOnly
import ru.org.linux.comment.{CommentDeleteService, CommentPrepareService, CommentReadService}
import ru.org.linux.search.SearchQueueSender
import ru.org.linux.site.{BadParameterException, Template}
import ru.org.linux.spring.dao.DeleteInfoDao
import ru.org.linux.user.{IgnoreListDao, UserErrorException}

import scala.collection.Seq

@Controller
class SolveTopicController(searchQueueSender: SearchQueueSender, commentService: CommentReadService,
                           topicDao: TopicDao, prepareService: CommentPrepareService,
                           topicService: TopicService,
                           permissionService: TopicPermissionService, commentDeleteService: CommentDeleteService,
                           deleteInfoDao: DeleteInfoDao, ignoreListDao: IgnoreListDao) extends StrictLogging {


  @RequestMapping(value = Array("/solve.jsp"), method = Array(RequestMethod.GET))
  def solveForm(@RequestParam("msgid") msgid: Int): ModelAndView = AuthorizedOnly { currentUser =>
    val tmpl = Template.getTemplate

    val comment = commentService.getById(msgid)
    if (comment.deleted) {
      throw new UserErrorException("комментарий уже удален")
    }

    val topic = topicDao.getById(comment.topicId)
    if (topic.deleted) {
      throw new AccessViolationException("тема удалена")
    }

    val haveAnswers = commentService.hasAnswers(comment)

    if (!permissionService.isCommentDeletableNow(comment, currentUser.user, topic, haveAnswers)) {
      throw new UserErrorException("комментарий нельзя удалить")
    }

    val comments = commentService.getCommentList(topic, currentUser.moderator)
    val list = commentService.getCommentsSubtree(comments, msgid, Set.empty[Int])

    val ignoreList = ignoreListDao.get(currentUser.user.getId)

    /*new ModelAndView("delete_comment", Map[String, Any](
      "msgid" -> msgid,
      "comments" -> comments,
      "topic" -> topic,
      "commentsPrepared" -> prepareService.prepareCommentList(comments, list, topic, Set.empty[Int],
        Some(currentUser.user), tmpl.getProf, ignoreList, filterShow = false).asJava
    ).asJava)*/
    null

  }


  @RequestMapping(value = Array("/solve.jsp"), method = Array(RequestMethod.POST))
  def solveTopic(@RequestParam("msgid") msgid: Int, @RequestParam("remark") remark: String,
                     @RequestParam(value = "bonus", defaultValue = "0") bonus: Int): ModelAndView = AuthorizedOnly { currentUser =>
    if (bonus < 0 || bonus > 20) {
      throw new BadParameterException("неправильный размер бонуса")
    }

    val user = currentUser.user

    val comment = commentService.getById(msgid)
    if (comment.deleted) {
      throw new UserErrorException("комментарий удален и не может быть указан как решение")
    }

    val topic = topicDao.getById(comment.topicId)

    topicService.solveWithBonus(topic, msgid, user, remark, bonus)



    /*    new ModelAndView("action-done", (Map(
      "message" -> message,
      "link" -> nextLink
    ) ++ bigMessage).asJava)*/
    null
  }



}
