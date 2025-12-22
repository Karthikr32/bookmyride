package com.BusReservation.utils;


public final class RegExPatterns {

    public static final String DATE_REGEX = "^((0[0-9]|1[0-9]|2[0-9]|3[0-1])[/-](0[0-9]|1[0-2])[/-][0-9]{4})|([0-9]{4}[/-](0[0-9]|1[0-2])[/-](0[0-9]|1[0-9]|2[0-9]|3[0-1]))$";

    public static final String MOBILE_REGEX = "^[6-9][0-9]{9}$";

    public static final String EMAIL_REGEX = "^[A-Za-z0-9._-]+@(gmail.com|yahoo.com)$";

    public static final String NAME_REGEX = "^[A-Z][a-z]+( [A-Z][a-z]+)*$";

    public static final String GENDER_REGEX = "(?i)^(MALE|FEMALE)$";

    public static final String ROLE_REGEX = "(?i)^(USER|ADMIN|GUEST)$";

    public static final String INTERNAL_ROLE_REGEX = "(?i)^(ADMIN)$";

    public static final String COST_WITH_DECIMAL_REGEX = "^([1-9][0-9]{2,}.[0-9]{2})$";

    public static final String COST_WITHOUT_DECIMAL_REGEX = "^([1-9][0-9]{2,})$";

    public static final String LOCATION_REGEX = "^[A-Z][a-z]+([- ][A-Z][a-z])*$";

    public static final String BUS_NUMBER_REGEX = "^(AP|AR|AS|BR|CG|CH|DL|GA|GJ|HR|HP|JH|KA|KL|MP|MH|MN|ML|MZ|NL|OD|PB|PY|RJ|SK|TN|TR|UP|UK|WB)(0[1-9]|[1-9][0-9])[A-Z]{2}[0-9]{4}$";

    public static final String TIME_REGEX = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])*$";

    public static final String BOOKING_STATUS_REGEX = "(?i)^(PENDING|PROCESSING|CONFIRMED|CANCELLED|EXPIRED)$";

    public static final String PAYMENT_STATUS_REGEX = "(?i)^(PAID|UNPAID|PENDING|FAILED)$";

    public static final String PAYMENT_METHOD_REGEX = "(?i)^(UPI|CARD|QR[_ ]CODE|BANK[_ ]TRANSFER|NET[_ ]BANKING)$";

    public static final String PAYMENT_METHOD_INTERNAL_REGEX = "(?i)^(UPI|CARD|QR[_ ]CODE|BANK[_ ]TRANSFER|NET[_ ]BANKING|NONE)$";

    public static final String BUS_TYPE_REGEX = "(?i)^(AC[_ ]SLEEPER|NON[_ ]AC[_ ]SLEEPER|AC[_ ]SEATER[_ ]SLEEPER|SEMI[_ ]SLEEPER|AC[_ ]SEMI[_ ]SLEEPER|DELUXE[_ ]AC|DELUXE[_ ]NON[_ ]AC|ULTRA[_ ]DELUXE|VOLVO[_ ]AC|SCANIA[_ ]AC|MERCEDES[_ ]BENZ|BHARAT[_ ]BENZ[_ ]AC|ELECTRIC[_ ]AC|ELECTRIC[_ ]NON[_ ]AC|MULTI[_ ]AXLE)$";

    public static final String STATE_REGEX = "(?i)^(ANDHRA[_ ]PRADESH|ARUNACHAL[_ ]PRADESH|ASSAM|BIHAR|CHHATTISGARH|CHANDIGARH|DELHI|GOA|GUJARAT|HARYANA|HIMACHAL[_ ]PRADESH|JHARKHAND|KARNATAKA|KERALA|MADHYA[_ ]PRADESH|MAHARASHTRA|MANIPUR|MEGHALAYA|MIZORAM|NAGALAND|ODISHA|PUNJAB|PUDUCHERRY|RAJASTHAN|SIKKIM|TAMIL[_ ]NADU|TELANGANA|TRIPURA|UTTAR[_ ]PRADESH|UTTARAKHAND|WEST[_ ]BENGAL)$";

    public static final String COUNTRY_REGEX = "(?i)^(INDIA)$";

    public static final String BASIC_BUS_TYPE_REGEX = "(?i)^(AC|NON[_ -]AC|SEATER|SLEEPER)$";

    public static final String AC_TYPE_REGEX = "(?i)^(AC|NON[_ -]AC)$";

    public static final String SEAT_TYPE_REGEX = "(?i)^(SEATER|SLEEPER)$";

    public static final String BUS_TICKET_REGEX = "^(TK)[0-9]{2}[a-f0-9]{8}$";

    public static final String TRANSACTION_REGEX = "^(TNX)[0-9]{2}([01][0-9]|2[0-3])([0-5][0-9]){2}[0-9]{3}[a-f0-9]{6}$";

    public static final String BUS_NAME_REGEX = "^[A-Z][A-Za-z0-9@()/&. ]+( [A-Za-z0-9@()/&. ]+)*$";

    public static final String PERMIT_STATUS_REGEX = "(?i)^(PERMITTED|NOT[_ ]PERMITTED)$";

    public static final String DEPARTURE_AT_REGEX = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";

    public static final String SORT_ORDER_REGEX = "(?i)^(ASC|DESC)$";

    public static final String TIME_RANGE_REGEX = "^(0[0-9]|1[0-9]|2[0-3])-(0[0-9]|1[0-9]|2[0-3])$";

    public static final String HUMAN_READABLE_DATE_REGEX = "^(0[0-9]|1[0-9]|2[0-9]|3[0-1])[/-](0[1-9]|1[0-2])[/-][0-9]{4}$";

    public static final String ADMIN_USERNAME_REGEX = "^(adm_)[a-z]{3,}_[a-f0-9]{4}$";

    public static final String ADMIN_PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%!&*]).{12,}$";

    public static final String USER_PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%!&*]).{10,}$";
}
