package com.example.helpinghood.controller;

import com.example.helpinghood.service.DemoDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    private final DemoDataService demoDataService;

    public ViewController(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @GetMapping("/")
    public String landing(Model model) {
        model.addAttribute("categories", demoDataService.categories());
        model.addAttribute("nearbyJobs", demoDataService.jobs().stream().limit(3).toList());
        return "pages/landing";
    }

    @GetMapping("/auth")
    public String auth() {
        return "pages/auth";
    }

    @GetMapping("/dashboard/giovane")
    public String youngDashboard(Model model) {
        model.addAttribute("stats", demoDataService.youngStats());
        model.addAttribute("jobs", demoDataService.jobs());
        model.addAttribute("profile", demoDataService.youngProfiles().get(0));
        return "pages/dashboard-young";
    }

    @GetMapping("/dashboard/richiedente")
    public String requesterDashboard(Model model) {
        model.addAttribute("stats", demoDataService.requesterStats());
        model.addAttribute("jobs", demoDataService.jobs());
        return "pages/dashboard-requester";
    }

    @GetMapping("/lavori")
    public String jobs(Model model) {
        model.addAttribute("jobs", demoDataService.jobs());
        model.addAttribute("categories", demoDataService.categories());
        return "pages/jobs";
    }

    @GetMapping("/lavori/{id}")
    public String jobDetail(@PathVariable long id, Model model) {
        model.addAttribute("job", demoDataService.jobById(id));
        model.addAttribute("reviews", demoDataService.reviews());
        return "pages/job-detail";
    }

    @GetMapping("/profili/giovani/{id}")
    public String youngProfile(@PathVariable long id, Model model) {
        model.addAttribute("profile", demoDataService.youngProfileById(id));
        model.addAttribute("reviews", demoDataService.reviews());
        return "pages/young-profile";
    }

    @GetMapping("/annunci/nuovo")
    public String newJob(Model model) {
        model.addAttribute("categories", demoDataService.categories());
        return "pages/new-job";
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        model.addAttribute("messages", demoDataService.chatMessages());
        return "pages/chat";
    }

    @GetMapping("/recensioni")
    public String reviews(Model model) {
        model.addAttribute("reviews", demoDataService.reviews());
        model.addAttribute("profiles", demoDataService.youngProfiles());
        return "pages/reviews";
    }
}
