package org.gitanimals.coupon.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.DisplayName
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource


@DataJpaTest
@ContextConfiguration(
    classes = [CouponService::class]
)
@TestPropertySource("classpath:test.properties")
@EntityScan(basePackages = ["org.gitanimals.coupon.domain"])
@EnableJpaRepositories(basePackages = ["org.gitanimals.coupon.domain"])
@DisplayName("CouponService 클래스의")
internal class CouponServiceTest(
    private val couponService: CouponService,
    private val couponRepository: CouponRepository,
    private val idempotencyRepository: IdempotencyRepository,
) : DescribeSpec({

    afterEach {
        couponRepository.deleteAll()
        idempotencyRepository.deleteAll()
    }

    describe("isValidCoupon 메소드는") {
        context("사용하지 않았으면서, 사용가능한 코드의 쿠폰이라면,") {
            val userId = 1L

            it("true를 반환한다.") {
                couponService.isValidCoupon(userId, VALID_COUPON_CODE) shouldBeEqual true
            }
        }

        context("사용할 수 없는 code의 쿠폰이라면,") {
            val userId = 1L
            val invalidCoupon = "IN_VALID_COUPON"

            it("false를 반환한다.") {
                couponService.isValidCoupon(userId, invalidCoupon) shouldBeEqual false
            }
        }

        context("이미 유저가 쿠폰을 사용했다면,") {
            val userId = 1L
            couponService.useCoupon(userId, VALID_COUPON_CODE)

            it("false를 반환한다.") {
                couponService.isValidCoupon(userId, VALID_COUPON_CODE) shouldBeEqual false
            }
        }
    }

    describe("useCoupon 메소드는") {
        context("사용하지 않은 쿠폰 코드가 들어오면,") {
            val userId = 1L

            it("쿠폰을 사용한다.") {
                shouldNotThrowAny { couponService.useCoupon(userId, VALID_COUPON_CODE) }
            }
        }

        context("이미 사용한 쿠폰 코드가 들어오면,") {
            val userId = 1L

            it("쿠폰을 사용하지 않고, 예외를 던진다.") {
                couponService.useCoupon(userId, VALID_COUPON_CODE)

                shouldThrowWithMessage<IllegalArgumentException>("Duplicated coupon use request") {
                    couponService.useCoupon(userId, VALID_COUPON_CODE)
                }
            }
        }
    }

    describe("deleteCoupon 메소드는") {
        context("유저의 아이디와 쿠폰코드를 받으면,") {
            val userId = 1L

            couponService.useCoupon(userId, VALID_COUPON_CODE)
            it("해당하는 쿠폰을 삭제한다.") {
                couponService.deleteCoupon(userId, VALID_COUPON_CODE)

                val coupons = couponRepository.findAll()
                coupons.isEmpty() shouldBeEqual true
            }
        }
    }
}) {

    companion object {
        private const val VALID_COUPON_CODE = "NEW_USER_BONUS_PET"
    }
}
