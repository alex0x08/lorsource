package ru.org.linux.topic


import com.typesafe.scalalogging.StrictLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RequestParam}
import org.springframework.web.servlet.ModelAndView
import ru.org.linux.auth.AuthUtil.AuthorizedOnly
import ru.org.linux.search.SearchQueueSender
import ru.org.linux.user.UserErrorException

@Controller
class SolutionTopicController(searchQueueSender: SearchQueueSender,
                              topicDao: TopicDao, topicService: TopicService) extends StrictLogging {

  @RequestMapping(value = Array("/solve.jsp"), method = Array(RequestMethod.POST))
  def solveMessage(@RequestParam("msgid") msgid: Int, @RequestParam("solutionid") solutionId: Int,
                    @RequestParam("remark") remark: String,
                    @RequestParam(value = "bonus", defaultValue = "0") bonus: Int): ModelAndView = AuthorizedOnly { currentUser =>
    val user = currentUser.user

    val message = topicDao.getById(msgid)
    if (message.solutionId>0) {
      throw new UserErrorException("Решение уже найдено")
    }

    topicService.solveWithBonus(message, solutionId, user, remark, bonus)
    logger.info(s"Указано $solutionId решение для $msgid пользователем ${user.getNick} , заметка: `$remark'")

    searchQueueSender.updateMessage(msgid, true)

    new ModelAndView("action-done", "message", "Решение найдено")
  }

}
