package com.example.demo.user.batch.writer

import com.example.demo.user.batch.mapper.DeleteUserItem
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.annotation.AfterStep
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class DeleteUserItemWriter(
	private val jdbcTemplate: JdbcTemplate
) : ItemWriter<DeleteUserItem> {
	override fun write(items: Chunk<out DeleteUserItem>) {
		items.forEach {
			logger.info { "Hard Deleted User By = ${it.name} ${it.email} ${it.role} ${it.deletedDt}" }

			jdbcTemplate.update(
				"DELETE FROM \"user\" WHERE user_id = ?",
				it.id
			)
		}
	}

	@BeforeStep
	fun beforeStep() {
		logger.info { ">>> Delete User Writer is starting" }
	}

	@AfterStep
	fun afterStep() {
		logger.info { ">>> Delete User Writer has finished" }
	}
}
