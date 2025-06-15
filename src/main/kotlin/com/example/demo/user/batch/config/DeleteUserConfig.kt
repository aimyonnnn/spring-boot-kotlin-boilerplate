package com.example.demo.user.batch.config

import com.example.demo.user.batch.mapper.DeleteUserItem
import com.example.demo.user.batch.processor.DeleteUserItemProcessor
import com.example.demo.user.batch.reader.DeleteUserItemReader
import com.example.demo.user.batch.writer.DeleteUserItemWriter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Configuration
class DeleteUserConfig(
	private val deleteUserItemReader: DeleteUserItemReader,
	private val deleteUserItemProcessor: DeleteUserItemProcessor,
	private val deleteUserItemWriter: DeleteUserItemWriter
) : DefaultBatchConfiguration() {
	private val chunkSize = 10

	@Bean
	fun deleteUser(
		jobRepository: JobRepository,
		transactionManager: PlatformTransactionManager
	): Job =
		JobBuilder("deleteUserJob", jobRepository)
			.start(deleteUserStep(jobRepository, transactionManager))
			.build()

	@Bean
	@JobScope
	fun deleteUserStep(
		jobRepository: JobRepository,
		transactionManager: PlatformTransactionManager
	): Step =
		StepBuilder("deleteUserStep", jobRepository)
			.chunk<DeleteUserItem, DeleteUserItem>(chunkSize, transactionManager)
			.reader(deleteUserReader(null))
			.processor(deleteUserProcessor())
			.writer(deleteUserWriter())
			.build()

	@Bean
	@StepScope
	fun deleteUserReader(
		@Value("#{jobParameters[now]}") now: LocalDateTime?
	): JdbcPagingItemReader<DeleteUserItem> {
		val nowDateTime = checkNotNull(now) { "now parameter is required" }

		return deleteUserItemReader.reader(chunkSize, nowDateTime)
	}

	@Bean
	@StepScope
	fun deleteUserProcessor(): DeleteUserItemProcessor = deleteUserItemProcessor

	@Bean
	@StepScope
	fun deleteUserWriter(): DeleteUserItemWriter = deleteUserItemWriter
}
