package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")

    private Long id;

    protected Order() {
    }

    @ManyToOne(fetch = FetchType.LAZY) // order 와 member 는 다대일
    @JoinColumn(name = "member_id") // 주문한 회원에 대한 정보 맵핑
    private Member member;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER , CANCEL]

    public void setMemeber(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    //생성 메서드

    public static Order createOrder(Member member ,Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();// order 객체 생성
        order.setMember(member); // 멤버 정보 설정
        order.setDelivery(delivery); // 주문 정보 설정
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem); //아이템 추가

        }
        order.setStatus(OrderStatus.ORDER); // 주문 상태 설정
        order.setOrderDate(LocalDateTime.now()); //현재 날짜 지정
        return order;




    }

    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){ //배송이 이미 시작되었으면,
            throw new IllegalStateException("배송이 이미 시작되었습니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //조회로직 - 전체 주문 가격 조회
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalprice();
        }
        return totalPrice;
    }








}
