import fs from "node:fs/promises";
import { Presentation, PresentationFile } from "@oai/artifact-tool";

const OUT = "D:/work/jie/adm/老板汇报_供应链人员分工架构图_最终版.pptx";
const PREVIEW = "D:/work/jie/adm/.tmp_boss_architecture/supplychain-roles.png";
const COLORS = {
  bg: "#F7FAFF", text: "#102A56", muted: "#62748A", primary: "#1677FF",
  operations: "#EAF3FF", supplier: "#FFF4E5", warehouse: "#EAFBF3", manager: "#F1EDFF",
  operationsLine: "#8CBFFF", supplierLine: "#F3BB67", warehouseLine: "#7BD3AA", managerLine: "#B29AF3",
};

async function saveBlob(path, blob) {
  await fs.writeFile(path, new Uint8Array(await blob.arrayBuffer()));
}

function text(slide, value, left, top, width, height, style = {}) {
  const shape = slide.shapes.add({
    geometry: "textbox",
    position: { left, top, width, height },
    fill: "none",
    line: { style: "solid", fill: "none", width: 0 },
  });
  shape.text = value;
  shape.text.style = {
    fontFace: "Microsoft YaHei", fontSize: 18, color: COLORS.text,
    marginLeft: 0, marginRight: 0, marginTop: 0, marginBottom: 0, ...style,
  };
  return shape;
}

function rect(slide, left, top, width, height, fill, line, radius = "rounded-xl") {
  return slide.shapes.add({
    geometry: "roundRect", position: { left, top, width, height }, fill,
    line: { style: "solid", fill: line, width: 1.2 }, borderRadius: radius,
  });
}

function roleCard(slide, x, y, width, height, fill, line, role, focus, tasks) {
  const card = rect(slide, x, y, width, height, fill, line);
  text(slide, role, x + 22, y + 18, width - 44, 32, { fontSize: 28, bold: true, alignment: "center" });
  text(slide, focus, x + 22, y + 56, width - 44, 26, { fontSize: 16, color: COLORS.muted, alignment: "center" });
  const taskTop = y + 104;
  tasks.forEach((task, index) => {
    text(slide, `• ${task}`, x + 20, taskTop + index * 36, width - 40, 26, { fontSize: 16, color: COLORS.text });
  });
  return card;
}

function step(slide, x, y, width, title, owner, fill, line) {
  const node = rect(slide, x, y, width, 60, fill, line);
  text(slide, title, x + 8, y + 10, width - 16, 22, { fontSize: 17, bold: true, alignment: "center" });
  text(slide, owner, x + 8, y + 35, width - 16, 16, { fontSize: 12, color: COLORS.muted, alignment: "center" });
  return node;
}

async function main() {
  const deck = Presentation.create({ slideSize: { width: 1280, height: 720 } });
  const slide = deck.slides.add();
  slide.background.fill = COLORS.bg;

  text(slide, "供应链人员分工架构：每个环节有人负责，每笔订单有据可查", 58, 28, 1160, 48, {
    fontSize: 38, bold: true, color: COLORS.text,
  });
  text(slide, "系统负责串联流程；人员聚焦各自专业动作，减少跨角色反复沟通", 60, 80, 900, 24, {
    fontSize: 20, color: COLORS.muted,
  });

  const manager = rect(slide, 58, 135, 1164, 70, COLORS.manager, COLORS.managerLine);
  text(slide, "管理者", 88, 154, 120, 26, { fontSize: 22, bold: true });
  text(slide, "查看订单全程状态、识别异常、协调跨角色问题", 230, 157, 760, 24, { fontSize: 18, color: COLORS.muted });
  text(slide, "不是逐单处理，而是对过程负责", 972, 158, 215, 22, { fontSize: 14, color: COLORS.primary, alignment: "right" });

  const cards = [
    roleCard(slide, 35, 230, 220, 250, COLORS.operations, COLORS.operationsLine, "运营人员", "订单创建与进度跟进", ["创建入仓订单", "匹配商品与供应商", "发起定向推单", "跟进异常并协调处理"]),
    roleCard(slide, 280, 230, 220, 250, COLORS.supplier, COLORS.supplierLine, "供应商", "确认供货并安排发货", ["查看待处理订单", "报价并确认供货", "安排发货", "提供物流单号"]),
    roleCard(slide, 525, 230, 220, 250, COLORS.manager, COLORS.managerLine, "财务人员", "供应商付款与状态回填", ["审核付款申请", "确认付款主体", "执行供应商打款", "回填付款状态"]),
    roleCard(slide, 770, 230, 220, 250, COLORS.warehouse, COLORS.warehouseLine, "仓库人员", "验收、入仓与串码确认", ["接收待入仓货物", "拣货与验货", "录入串码和仓库", "确认入仓结果"]),
    roleCard(slide, 1015, 230, 220, 250, "#FFFFFF", "#BDD0EC", "系统", "统一记录订单流转", ["自动保留处理记录", "实时更新订单状态", "汇总物流入仓信息", "让责任与进度可见"]),
  ];

  const deliverySteps = [
    step(slide, 115, 505, 125, "审核订单", "运营人员", COLORS.operations, COLORS.operationsLine),
    step(slide, 265, 505, 125, "报价", "供应商", COLORS.supplier, COLORS.supplierLine),
    step(slide, 415, 505, 125, "供应商供货", "供应商", COLORS.supplier, COLORS.supplierLine),
    step(slide, 565, 505, 125, "发货", "供应商", COLORS.supplier, COLORS.supplierLine),
    step(slide, 715, 505, 125, "物流跟进", "运营人员", COLORS.operations, COLORS.operationsLine),
    step(slide, 865, 505, 125, "订单完成", "系统记录", "#FFFFFF", "#BDD0EC"),
    step(slide, 1015, 505, 125, "财务打款", "财务人员", COLORS.manager, COLORS.managerLine),
  ];
  const warehousingSteps = [
    step(slide, 115, 583, 125, "审核订单", "运营人员", COLORS.operations, COLORS.operationsLine),
    step(slide, 265, 583, 125, "报价", "供应商", COLORS.supplier, COLORS.supplierLine),
    step(slide, 415, 583, 125, "供应商供货", "供应商", COLORS.supplier, COLORS.supplierLine),
    step(slide, 565, 583, 125, "送货入仓", "供应商", COLORS.supplier, COLORS.supplierLine),
    step(slide, 715, 583, 125, "验货入仓", "仓库人员", COLORS.warehouse, COLORS.warehouseLine),
    step(slide, 865, 583, 125, "订单完成", "系统记录", "#FFFFFF", "#BDD0EC"),
    step(slide, 1015, 583, 125, "财务打款", "财务人员", COLORS.manager, COLORS.managerLine),
  ];
  [deliverySteps, warehousingSteps].forEach((flow) => {
    for (let index = 0; index < flow.length - 1; index += 1) {
      slide.shapes.connect(flow[index], flow[index + 1], {
        kind: "straight", fromSide: "right", toSide: "left",
        line: { style: "solid", fill: COLORS.primary, width: 2 },
        tail: { type: "arrow", width: "sm", length: "sm" },
      });
    }
  });
  text(slide, "代发业务", 40, 523, 58, 20, { fontSize: 16, bold: true, color: COLORS.primary, alignment: "center" });
  text(slide, "入仓业务", 40, 601, 58, 20, { fontSize: 16, bold: true, color: COLORS.primary, alignment: "center" });
  text(slide, "核心变化：两类订单分线流转，履约完成后统一打款，系统完整记录每一步", 58, 670, 1164, 24, {
    fontSize: 19, bold: true, color: COLORS.primary, alignment: "center",
  });
  text(slide, "无界电商供应链协同", 60, 692, 300, 16, { fontSize: 12, color: COLORS.muted });

  slide.speakerNotes.textFrame.setText([
    "[讲解稿｜约10分钟]",
    "开场：这页不讲技术，只讲供应链中谁负责什么。系统的作用是把角色、动作和订单状态串起来。",
    "管理者：不需要逐单处理，但能够看到订单全程、识别异常并协调问题。",
    "运营人员：负责订单发起、匹配商品与供应商、推单和异常跟进，是流程的发起与协同角色。",
    "供应商：负责报价确认、供货和发货，把供货承诺与物流信息沉淀到系统。",
    "财务人员：在订单完成后审核付款申请、执行供应商打款，并回填付款状态。",
    "仓库人员：负责收到货后的验货、拣货、入仓和串码确认，确保实际货物与订单一致。",
    "订单流转：代发为审核订单、报价、供货、发货、物流跟进、订单完成、财务打款；入仓为审核订单、报价、供货、送货入仓、验货入仓、订单完成、财务打款。",
    "系统：不替代人的专业判断，而是自动保留处理记录、更新状态，让每个人只看到自己该处理的事情。",
    "收尾：过去靠人催和表格记，现在按角色分工、按订单状态协同，责任更清楚，过程也更可追溯。",
    "[Sources] 内部项目现有供应链菜单、角色协作流程与订单处理能力；本页不引用外部统计数据。",
  ]);
  slide.speakerNotes.setVisible(true);

  await saveBlob(PREVIEW, await deck.export({ slide, format: "png", scale: 1 }));
  const pptx = await PresentationFile.exportPptx(deck);
  await pptx.save(OUT);
}

main().catch((error) => { console.error(error); process.exitCode = 1; });
