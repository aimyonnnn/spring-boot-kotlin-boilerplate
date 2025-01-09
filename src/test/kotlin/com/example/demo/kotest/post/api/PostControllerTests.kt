package com.example.demo.kotest.post.api

import com.example.demo.post.api.PostController
import com.example.demo.post.application.ChangePostService
import com.example.demo.post.application.GetPostService
import com.example.demo.post.dto.serve.request.CreatePostRequest
import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.request.UpdatePostRequest
import com.example.demo.post.dto.serve.response.CreatePostResponse
import com.example.demo.post.dto.serve.response.GetPostResponse
import com.example.demo.post.dto.serve.response.UpdatePostResponse
import com.example.demo.post.entity.Post
import com.example.demo.security.SecurityUserItem
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.instancio.Instancio
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Tags("kotest-unit-test")
class PostControllerTests :
  FunSpec({
    val postController = mockk<PostController>()
    val getPostService = mockk<GetPostService>()
    val changePostService = mockk<ChangePostService>()

    val post: Post = Instancio.create(Post::class.java)
    val defaultPageable = Pageable.ofSize(1)

    test("Get Post By Id") {

      every { getPostService.getPostById(any<Long>()) } returns GetPostResponse.of(post)

      every { postController.getPostById(any<Long>()) } returns ResponseEntity.ok(GetPostResponse.of(post))

      val response = postController.getPostById(post.id)

      response shouldNotBeNull {
        statusCode shouldBe HttpStatus.OK
        body shouldNotBeNull {
          postId shouldBe post.id
          title shouldBe post.title
          subTitle shouldBe post.subTitle
          content shouldBe post.content
          writer.userId shouldBe post.user.id
        }
      }
    }

    test("Get Post List") {

      every { getPostService.getPostList(any<Pageable>()) } returns
        PageImpl(
          listOf(GetPostResponse.of(post)),
          defaultPageable,
          1
        )

      every {
        postController.getPostList(any<Pageable>())
      } returns ResponseEntity.ok(PageImpl(listOf(GetPostResponse.of(post)), defaultPageable, 1))

      val response =
        postController.getPostList(
          defaultPageable
        )

      response shouldNotBeNull {
        statusCode shouldBe HttpStatus.OK
        body shouldNotBeNull {
          content[0] shouldNotBeNull {
            postId shouldBe post.id
            title shouldBe post.title
            subTitle shouldBe post.subTitle
            content shouldBe post.content
            writer.userId shouldBe post.user.id
          }
        }
      }
    }

    test("Get Exclude Users Post List") {
      val getExcludeUsersPostsRequest =
        Instancio.create(
          GetExcludeUsersPostsRequest::class.java
        )

      every {
        getPostService.getExcludeUsersPostList(
          any<GetExcludeUsersPostsRequest>(),
          any<Pageable>()
        )
      } returns
        PageImpl(
          listOf(GetPostResponse.of(post)),
          defaultPageable,
          1
        )

      every {
        postController.getExcludeUsersPostList(any<GetExcludeUsersPostsRequest>(), any<Pageable>())
      } returns ResponseEntity.ok(PageImpl(listOf(GetPostResponse.of(post)), defaultPageable, 1))

      val response =
        postController.getExcludeUsersPostList(
          getExcludeUsersPostsRequest,
          defaultPageable
        )

      response shouldNotBeNull {
        statusCode shouldBe HttpStatus.OK
        body shouldNotBeNull {
          content[0] shouldNotBeNull {
            postId shouldBe post.id
            title shouldBe post.title
            subTitle shouldBe post.subTitle
            content shouldBe post.content
            writer.userId shouldBe post.user.id
          }
        }
      }
    }

    test("Create Post") {
      val createPostRequest =
        Instancio.create(
          CreatePostRequest::class.java
        )
      val securityUserItem =
        Instancio.create(
          SecurityUserItem::class.java
        )

      every { changePostService.createPost(any<Long>(), any<CreatePostRequest>()) } returns CreatePostResponse.of(post)

      every {
        postController.createPost(
          any<CreatePostRequest>(),
          any<SecurityUserItem>()
        )
      } returns ResponseEntity.status(HttpStatus.CREATED).body(CreatePostResponse.of(post))

      val response = postController.createPost(createPostRequest, securityUserItem)

      response shouldNotBeNull {
        statusCode shouldBe HttpStatus.CREATED
        body shouldNotBeNull {
          postId shouldBe post.id
          title shouldBe post.title
          subTitle shouldBe post.subTitle
          content shouldBe post.content
          writer.userId shouldBe post.user.id
        }
      }
    }

    test("Update Post") {
      val updatedPostRequest = Instancio.create(UpdatePostRequest::class.java)

      every { changePostService.updatePost(any<Long>(), any<UpdatePostRequest>()) } returns UpdatePostResponse.of(post)

      every { postController.updatePost(any<UpdatePostRequest>(), any<Long>()) } returns
        ResponseEntity.ok(
          UpdatePostResponse.of(post)
        )

      val response = postController.updatePost(updatedPostRequest, post.id)

      response shouldNotBeNull {
        statusCode shouldBe HttpStatus.OK
        body shouldNotBeNull {
          postId shouldBe post.id
          title shouldBe post.title
          subTitle shouldBe post.subTitle
          content shouldBe post.content
          writer.userId shouldBe post.user.id
        }
      }
    }

    test("Delete Post") {

      justRun { changePostService.deletePost(any<Long>()) }

      every { postController.deletePost(any<Long>()) } returns ResponseEntity.noContent().build()

      val response = postController.deletePost(post.id)

      response shouldNotBeNull {
        body.shouldBeNull()
        statusCode shouldBe HttpStatus.NO_CONTENT
      }
    }
  })
