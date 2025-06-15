package com.example.demo.user.batch.reader

import com.example.demo.user.batch.mapper.DeleteUserItem
import com.example.demo.user.batch.mapper.DeleteUserItemRowMapper
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.sql.DataSource

@Component
class DeleteUserItemReader(
	private val dataSource: DataSource
) {
	fun reader(
		chunkSize: Int,
		now: LocalDateTime
	): JdbcPagingItemReader<DeleteUserItem> =
		JdbcPagingItemReaderBuilder<DeleteUserItem>()
			.name("DeletedUsersYearAgoReader")
			.dataSource(dataSource)
			.pageSize(chunkSize)
			.fetchSize(chunkSize)
			.queryProvider(pagingQueryProvider())
			.parameterValues(mapOf("oneYearBeforeNow" to now.minusYears(1) as Any))
			.rowMapper(DeleteUserItemRowMapper())
			.build()

	private fun pagingQueryProvider(): PagingQueryProvider {
		val queryProvider = SqlPagingQueryProviderFactoryBean()
		queryProvider.setDataSource(dataSource)
		queryProvider.setSelectClause("SELECT *")
		queryProvider.setFromClause("FROM \"user\"")
		queryProvider.setWhereClause("WHERE deleted_dt <= :oneYearBeforeNow")
		queryProvider.setSortKeys(mapOf("user_id" to Order.ASCENDING))
		return queryProvider.`object`
	}
}
