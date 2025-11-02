---
subcategory: "Content Delivery Network (CDN)"
layout: "huaweicloud"
page_title: "HuaweiCloud: huaweicloud_cdn_ip_information"
description: |-
  Use this data source to get the IP attribution information of CDN nodes within HuaweiCloud.  
---

[//]: # (标记为 title 的 name)
# huaweicloud_cdn_ip_information

[//]: # (标记为 title 的 description)
Use this data source to get the IP attribution information of CDN nodes within HuaweiCloud.

[//]: # (标记为 ExampleUsage 的 name)
## Example Usage

[//]: # (标记为 ExampleUsage 的 code)
```hcl
variable "ip_list_str" {}

data "huaweicloud_cdn_ip_information" "test" {
  ips = var.ip_list_str
}
```

[//]: # (标记为 ArgumentList 的 name )
## Argument Reference

[//]: # (标记为 ArgumentList 的 description)
The following arguments are supported:

[//]: # (标记为 Argument)
[//]: # (ips 被标记为 name)
[//]: # (Required，String 被标记为tags)
[//]: # (Specifies the list of IP addresses to be queried.  被标记为 description)
[//]: # (The maximum number of IPs that can be queried is `20`... 被标记为 subDomainList, 每行一个 Domain)
* `ips` - (Required, String) Specifies the list of IP addresses to be queried.  
  The maximum number of IPs that can be queried is `20`, and multiple IPs are separated by commas (,).

* `enterprise_project_id` - (Optional, String) Specifies the ID of the enterprise project to which the resource
  belongs.

[//]: # (标记为 AttributeList 的 name )
## Attribute Reference

[//]: # (标记为 AttributeList 的 description)
In addition to all arguments above, the following attributes are exported:

[//]: # (标记为 Attribute)
[//]: # (id 被标记为 name)
[//]: # (The data source ID.被标记为 description)
* `id` - The data source ID.

[//]: # (标记为 Attribute)
[//]: # (information 被标记为 name)
[//]: # (The list of IP attribution information that matched filter parameters.  被标记为 description)
[//]: # (  The [information]\(#cdn_information\) structure is documented below.被标记为 subDomainList, 每行一个 Domain)
* `information` - The list of IP attribution information that matched filter parameters.  
  The [information](#cdn_information) structure is documented below.

<a name="cdn_information"></a>
The `information` block supports:

* `ip` - The IP address to be queried.

* `belongs` - Whether the IP belongs to CDN nodes.
    + **true**
    + **false**

* `region` - The province where the IP is located.  
  "Unknown" indicates that the attribution is unknown.

* `isp` - The ISP name.  
  If the IP attribution is unknown, this field returns null.

* `platform` - The platform name.  
  If the IP attribution is unknown, this field returns null.