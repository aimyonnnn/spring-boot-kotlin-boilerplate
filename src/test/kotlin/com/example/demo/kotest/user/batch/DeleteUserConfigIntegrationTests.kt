package com.example.demo.kotest.user.batch

import com.example.demo.common.config.TestBatchConfig
import com.example.demo.user.batch.config.DeleteUserConfig
import com.example.demo.user.batch.mapper.DeleteUserItemRowMapper
import com.example.demo.user.constant.UserRole
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@Tags("kotest-integration-test")
@SpringBootTest(classes = [DeleteUserConfig::class, TestBatchConfig::class])
@SpringBatchTest
class DeleteUserConfigIntegrationTests(
  @Autowired
  private val jdbcTemplate: JdbcTemplate,
  @Autowired
  private val jobLauncherTestUtils: JobLauncherTestUtils,
  @Autowired
  private val jobRepositoryTestUtils: JobRepositoryTestUtils
) : FunSpec({
  val defaultUserEmail = "awakelife93@gmail.com"
  val defaultUserEncodePassword = "$2a$10\$T44NRNpbxkQ9qHbCtqQZ7O3gYfipzC0cHvOIJ/aV4PTlvJjtDl7x2\n" +  //
    ""
  val defaultUserName = "Hyunwoo Park"
  val defaultUserRole = UserRole.USER

  afterEach {
    jobRepositoryTestUtils.removeJobExecutions()
  }

  test("Verify the batch status, exit status, and list of deleted users when the given LocalDateTime matches the current time.") {
    val now = LocalDateTime.now().withNano(0)

    jdbcTemplate.update(
      "insert into \"user\" (created_dt, updated_dt, deleted_dt, email, name, password, role) values (?, ?, ?, ?, ?, ?, ?)",
      now,
      now,
      now.minusYears(1),
      defaultUserEmail,
      defaultUserName,
      defaultUserEncodePassword,
      defaultUserRole.name
    )

    val jobParameters = JobParametersBuilder()
      .addLocalDateTime("now", LocalDateTime.now())
      .toJobParameters()

    val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

    val deleteUserItemList = jdbcTemplate.query(
      "select * from \"user\" where deleted_dt <= ?",
      DeleteUserItemRowMapper(),
      now.minusYears(1)
    )

    jobExecution.status shouldBe BatchStatus.COMPLETED
    jobExecution.exitStatus shouldBe ExitStatus.COMPLETED
    deleteUserItemList.isEmpty() shouldBe true
  }
})
