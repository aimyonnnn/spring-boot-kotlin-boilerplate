package com.example.demo.kotest.post.application

import com.example.demo.post.application.GetPostService
import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.response.GetPostResponse
import com.example.demo.post.entity.Post
import com.example.demo.post.exception.PostNotFoundException
import com.example.demo.post.repository.PostRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.instancio.Instancio
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class GetPostServiceTests :
  BehaviorSpec({
    val postRepository = mockk<PostRepository>()
    val getPostService = mockk<GetPostService>()

    val post: Post = Instancio.create(Post::class.java)
    val defaultPageable = Pageable.ofSize(1)

    Given("Get Post By Id") {

      When("Success Get Post By Id") {

        every { postRepository.findOneById(any<Long>()) } returns post

        every {
          getPostService.getPostById(
            any<Long>()
          )
        } returns GetPostResponse.of(post)

        val getPostResponse = getPostService.getPostById(post.id)

        Then("Assert Post Entity") {
          getPostResponse shouldNotBeNull {
            postId shouldBe post.id
            title shouldBe post.title
            subTitle shouldBe post.subTitle
            content shouldBe post.content
            writer.userId shouldBe post.user.id
          }
        }
      }

      When("Post Not Found Exception") {

        every { postRepository.findOneById(any<Long>()) } returns null

        every { getPostService.getPostById(any<Long>()) } throws PostNotFoundException(post.id)

        shouldThrowExactly<PostNotFoundException> {
          getPostService.getPostById(post.id)
        }
      }
    }

    Given("Get Post List") {

      When("Success Get Post List") {
        every { postRepository.findAll(any<Pageable>()) } returns PageImpl(listOf(post), defaultPageable, 1)

        every {
          getPostService.getPostList(
            any<Pageable>()
          )
        } returns PageImpl(listOf(GetPostResponse.of(post)), defaultPageable, 1)

        val getPostResponseList =
          getPostService.getPostList(
            defaultPageable
          )

        Then("Assert Post List") {
          getPostResponseList.shouldNotBeEmpty()
          getPostResponseList.content[0] shouldNotBeNull {
            postId shouldBe post.id
            title shouldBe post.title
            subTitle shouldBe post.subTitle
            content shouldBe post.content
            writer.userId shouldBe post.user.id
          }
        }
      }
    }

    Given("Get Exclude Users Post List") {
      val getExcludeUsersPostsRequest: GetExcludeUsersPostsRequest =
        Instancio.create(
          GetExcludeUsersPostsRequest::class.java
        )

      When("Success Get Exclude Users Post List") {

        every {
          postRepository.getExcludeUsersPosts(
            any<GetExcludeUsersPostsRequest>(),
            any<Pageable>()
          )
        } returns PageImpl(listOf(GetPostResponse.of(post)), defaultPageable, 1)

        every {
          getPostService.getExcludeUsersPostList(
            any<GetExcludeUsersPostsRequest>(),
            any<Pageable>()
          )
        } returns PageImpl(listOf(GetPostResponse.of(post)), defaultPageable, 1)

        val getPostResponseList =
          getPostService.getExcludeUsersPostList(
            getExcludeUsersPostsRequest,
            defaultPageable
          )

        Then("Assert Post List") {
          getPostResponseList.shouldNotBeEmpty()
          getPostResponseList.content[0] shouldNotBeNull {
            postId shouldBe post.id
            title shouldBe post.title
            subTitle shouldBe post.subTitle
            content shouldBe post.content
            writer.userId shouldBe post.user.id
          }
        }
      }
    }
  })
