package com.example.demo.kotest.post.repository

import com.example.demo.common.config.JpaAuditConfig
import com.example.demo.common.config.QueryDslConfig
import com.example.demo.kotest.common.security.SecurityListenerFactory
import com.example.demo.post.dto.serve.request.UpdatePostRequest
import com.example.demo.post.entity.Post
import com.example.demo.post.repository.PostRepository
import com.example.demo.user.constant.UserRole
import com.example.demo.user.entity.User
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.instancio.Instancio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
@Import(value = [QueryDslConfig::class, JpaAuditConfig::class])
@DataJpaTest
class PostRepositoryTests(
  @Autowired
  private val postRepository: PostRepository
) : DescribeSpec({
    lateinit var postEntity: Post
    val defaultPostTitle = "unit test"
    val defaultPostSubTitle = "Post Repository Test"
    val defaultPostContent = "default value for post repository testing"
    val defaultUserEmail = "awakelife93@gmail.com"
    val defaultUserPassword = "test_password_123!@"
    val defaultUserName = "Hyunwoo Park"
    val defaultUserRole = UserRole.USER

    listeners(SecurityListenerFactory())

    beforeContainer {
      postEntity =
        Post(
          defaultPostTitle,
          defaultPostSubTitle,
          defaultPostContent,
          User(
            defaultUserEmail,
            defaultUserName,
            defaultUserPassword,
            defaultUserRole
          )
        )
    }

    describe("Create post") {

      context("Save post") {
        val createPost = postRepository.save(postEntity)

        it("Assert Post Entity") {
          createPost.id shouldBe postEntity.id
          createPost.title shouldBe postEntity.title
          createPost.subTitle shouldBe postEntity.subTitle
          createPost.content shouldBe postEntity.content
          createPost.user.id shouldBe postEntity.user.id
        }
      }
    }

    describe("Update post") {

      context("Save post") {
        val updatePostRequest =
          Instancio.create(
            UpdatePostRequest::class.java
          )

        val beforeUpdatePost = postRepository.save(postEntity)

        postRepository.save(
          beforeUpdatePost.update(
            updatePostRequest.title,
            updatePostRequest.subTitle,
            updatePostRequest.content
          )
        )

        val afterUpdatePost: Post =
          requireNotNull(
            postRepository
              .findOneById(beforeUpdatePost.id)
          ) {
            "Post must not be null"
          }

        it("Assert Post Entity") {
          afterUpdatePost.title shouldBe updatePostRequest.title
          afterUpdatePost.subTitle shouldBe updatePostRequest.subTitle
          afterUpdatePost.content shouldBe updatePostRequest.content
        }
      }
    }

    describe("Delete Post") {

      context("Call deleteById") {
        val beforeDeletePost = postRepository.save(postEntity)

        postRepository.deleteById(beforeDeletePost.id)

        val afterDeletePost: Post? =
          postRepository
            .findOneById(beforeDeletePost.id)

        it("Assert Null") {
          afterDeletePost.shouldBeNull()
        }
      }
    }

    describe("Find Post By Id") {

      context("Call findOneById") {
        val beforeFindPost = postRepository.save(postEntity)

        val afterFindPost: Post =
          requireNotNull(
            postRepository
              .findOneById(beforeFindPost.id)
          ) {
            "Post must not be null"
          }

        it("Assert Post Entity") {
          afterFindPost.id shouldBe beforeFindPost.id
          afterFindPost.title shouldBe beforeFindPost.title
          afterFindPost.subTitle shouldBe beforeFindPost.subTitle
          afterFindPost.content shouldBe beforeFindPost.content
          afterFindPost.user.id shouldBe beforeFindPost.user.id
        }
      }
    }

    describe("Find Post By user") {

      context("Call findOneByUser") {
        val beforeFindPost = postRepository.save(postEntity)

        val afterFindPost: Post =
          requireNotNull(
            postRepository
              .findOneByUser(beforeFindPost.user)
          ) {
            "Post must not be null"
          }

        it("Assert Post Entity") {
          afterFindPost.id shouldBe beforeFindPost.id
          afterFindPost.title shouldBe beforeFindPost.title
          afterFindPost.subTitle shouldBe beforeFindPost.subTitle
          afterFindPost.content shouldBe beforeFindPost.content
          afterFindPost.user.id shouldBe beforeFindPost.user.id
        }
      }
    }
  })
