package com.example.demo.post.entity

import com.example.demo.common.entity.BaseChangerEntity
import com.example.demo.user.entity.User
import jakarta.persistence.AttributeOverride
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "\"post\"")
@AttributeOverride(name = "id", column = Column(name = "post_id"))
data class Post(
  @Column(nullable = false, length = 20)
  var title: String,
  @Column(nullable = false, length = 40)
  var subTitle: String,
  @Column(nullable = false, length = 500)
  var content: String,
  @JoinColumn(name = "user_id")
  @ManyToOne(
    fetch = FetchType.LAZY,
    cascade = [CascadeType.PERSIST]
  )
  val user: User
) : BaseChangerEntity() {
  fun update(
    title: String,
    subTitle: String,
    content: String
  ): Post {
    this.title = title
    this.subTitle = subTitle
    this.content = content
    return this
  }
}
