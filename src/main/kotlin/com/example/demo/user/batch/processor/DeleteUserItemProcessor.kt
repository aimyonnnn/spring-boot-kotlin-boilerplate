package com.example.demo.user.batch.processor

import com.example.demo.user.batch.mapper.DeleteUserItem
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.annotation.AfterProcess
import org.springframework.batch.core.annotation.BeforeProcess
import org.springframework.batch.core.annotation.OnProcessError
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class DeleteUserItemProcessor : ItemProcessor<DeleteUserItem, DeleteUserItem> {
	override fun process(item: DeleteUserItem): DeleteUserItem {
		logger.info { "Processing user: ${item.id}" }
		return item
	}

	@BeforeProcess
	fun beforeProcess(item: DeleteUserItem) {
		logger.info { "Before processing: $item" }
	}

	@AfterProcess
	fun afterProcess(
		item: DeleteUserItem,
		result: DeleteUserItem?
	) {
		logger.info { "After processing: $item -> $result" }
	}

	@OnProcessError
	fun onError(
		item: DeleteUserItem,
		exception: Exception
	) {
		logger.error { "Error processing $item: ${exception.message}" }
	}
}
