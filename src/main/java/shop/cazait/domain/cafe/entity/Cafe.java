package shop.cazait.domain.cafe.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.cazait.domain.cafecongestion.entity.cafeCongestion;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "congestion_id")
    private cafeCongestion cafeCongestion;

    private String name;

    private String location;

    private double longitude;

    private double latitude;

    @Enumerated(EnumType.STRING)
    private cafeStatus status;
}
