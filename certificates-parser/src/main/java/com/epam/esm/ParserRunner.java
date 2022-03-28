package com.epam.esm;

import com.epam.esm.config.ParserProps;
import com.epam.esm.fio.FileTreeWalkingTask;
import com.epam.esm.fio.FileUtil;
import com.epam.esm.fio.parsing.ParsingTaskFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ParserRunner implements CommandLineRunner {
    private final ParsingTaskFactory parsingTaskFactory;
    private final ParserProps parserProps;
    private final UserService userService;

    @Autowired
    public ParserRunner(ParsingTaskFactory parsingTaskFactory, ParserProps parserProps, UserService userService) {
        this.parsingTaskFactory = parsingTaskFactory;
        this.parserProps = parserProps;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws IOException {
        authorizeDatabaseRequests();
        FileUtil.createDirectoryIfNotExists(parserProps.getErrorFolderPath());
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(1);
        Integer threadCount = parserProps.getThreadCount();
        Path rootFolderPath = Path.of(parserProps.getRootFolderPath());
        scheduledExecutorService.scheduleAtFixedRate(
                new FileTreeWalkingTask(rootFolderPath, threadCount, (parsingTaskFactory::createParsingTask)),
                0,
                (long) (parserProps.getScanDelay() * 1000),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Required for auditing purposes
     */
    private void authorizeDatabaseRequests() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        UserDetails userDetails = userService.loadUserByUsername(parserProps.getInternalAdminName());
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
