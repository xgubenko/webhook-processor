package webhook.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**

Order example:

{
  "clientId": "string",
  "board": "string",
  "securityCode": "string",
  "buySell": "Sell",
  "quantity": 0,
  "useCredit": true,
  "price": 0,
  "property": "PutInQueue",
  "condition": {
    "type": "Bid",
    "price": 0,
    "time": "2022-11-28T11:23:39.251Z"
  },
  "validBefore": {
    "type": "TillEndSession",
    "time": "2022-11-28T11:23:39.251Z"
  }
}
 **/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewOrder {

    //Trade Account ID Идентификатор торгового счёта
    private String clientId;

    //Trading Board Режим торгов
    private String board;

    //Security Code Тикер инструмента
    private String securityCode;

    //Transaction direction Направление сделки
    private TransactionDirection buySell;

    //Order volume in lots Количество лотов инструмента для заявки
    private int quantity;

    //Use credit. Not available in derivative market Использовать кредит. Недоступно для срочного рынка
    private boolean useCredit;

    //Order price. Use "null" to place Market Order Цена заявки. Используйте "null", чтобы выставить рыночную заявку
    private Double price;

    //Order placement properties Поведение заявки при выставлении в стакан
    private OrderPlacement property;

    //Order placement properties Свойства выставления заявок
    private OrderCondition condition;

    //Order time condition Условие по времени действия заявки
    private OrderValidBefore validBefore;
}
