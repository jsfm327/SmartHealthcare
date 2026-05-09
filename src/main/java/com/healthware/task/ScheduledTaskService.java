package com.healthware.task;

import com.healthware.entity.User;
import com.healthware.mapper.RegistrationMapper;
import com.healthware.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    /**
     * 每30分钟执行一次：解锁锁定超过30分钟的用户
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void autoUnlockUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(1800);
        List<User> lockedUsers = userMapper.selectLockedUsersBefore(threshold);
        for (User user : lockedUsers) {
            userMapper.unlockUser(user.getId());
        }
        if (!lockedUsers.isEmpty()) {
            System.out.println("[定时任务] 自动解锁 " + lockedUsers.size() + " 个用户");
        }
    }

    /**
     * 每天零点执行：将过期的待就诊挂号标记为已过期
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void expirePastRegistrations() {
        String today = LocalDate.now().toString();
        int count = registrationMapper.expirePastRegistrations(today);
        if (count > 0) {
            System.out.println("[定时任务] 自动过期 " + count + " 条挂号记录");
        }
    }
}
