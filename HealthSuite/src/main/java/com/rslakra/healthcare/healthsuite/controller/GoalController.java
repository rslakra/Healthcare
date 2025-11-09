package com.rslakra.healthcare.healthsuite.controller;

import com.rslakra.healthcare.healthsuite.model.Goal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

/**
 * @author rlakra
 */
@Controller
@SessionAttributes("goal")
public class GoalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoalController.class);

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "addGoal", method = RequestMethod.GET)
    public String addGoal(Model model) {
        LOGGER.debug("+addGoal({})", model);
        Goal goal = new Goal();
        goal.setMinutes(10);
        model.addAttribute("goal", goal);

        LOGGER.debug("-addGoal(), model={}", model);
        return "addGoal";
    }

    /**
     * @param goal
     * @param result
     * @return
     */
    @RequestMapping(value = "addGoal", method = RequestMethod.POST)
    public String updateGoal(@Valid @ModelAttribute("goal") Goal goal, BindingResult result) {
        LOGGER.debug("+updateGoal({}, {})", goal, result);


        LOGGER.debug("result={}, hasErrors={}, , goal.getMinutes={}", result, result.hasErrors(), goal.getMinutes());
        if (result.hasErrors()) {
            return "addGoal";
        }

        LOGGER.debug("-updateGoal(), redirect to index page.");
        return "redirect:index.jsp";
    }

}
