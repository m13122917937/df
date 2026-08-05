import fs from "node:fs/promises";
import { Presentation, PresentationFile } from "@oai/artifact-tool";

const OUT = "D:/work/jie/adm/老板汇报_供应链业务架构图.pptx";
const PREVIEW = "D:/work/jie/adm/.tmp_boss_architecture/slide-1.png";
const W = 1280;
const H = 720;
const PRIMARY = "#1677FF";
const PRIMARY_DARK = "#0B5ED7";
const TEXT = "#12233F";
const MUTED = "#5F6B7A";
const BORDER = "#C9D8F2";
const BG = "#F7FAFF";
const GREEN = "#19A974";
const ORANGE = "#F59E0B";

async function saveBlob(path, blob) {
  await fs.writeFile(path, new Uint8Array(await blob.arrayBuffer()));
}

function addText(slide, text, left, top, width, height, style = {}) {
  const shape = slide.shapes.add({
    geometry: "textbox",
    position: { left, top, width, height },
    fill: "none",
    line: { style: "solid", fill: "none", width: 0 },
  });
  shape.text = text;
  shape.text.style = {
    fontFace: "Microsoft YaHei",
    fontSize: 18,
    color: TEXT,
    marginLeft: 0,
    marginRight: 0,
    marginTop: 0,
    marginBottom: 0,
    ...style,
  };
  return shape;
}

function addBox(slide, { left, top, width, height, fill = "#FFFFFF", line = BORDER, radius = "rounded-xl", title, subtitle, titleColor = TEXT, subtitleColor = MUTED }) {
  const box = slide.shapes.add({
    geometry: "roundRect",
    position: { left, top, width, height },
    fill,
    line: { style: "solid", fill: line, width: 1.2 },
    borderRadius: radius,
  });
  addText(slide, title, left + 12, top + 10, width - 24, 25, {
    fontSize: 18,
    bold: true,
    color: titleColor,
    alignment: "center",
  });
  if (subtitle && height >= 55) {
    addText(slide, subtitle, left + 12, top + 38, width - 24, height - 44, {
      fontSize: 12,
      color: subtitleColor,
      alignment: "center",
      verticalAlignment: "middle",
    });
  }
  return box;
}

function addBand(slide, left, top, width, height, label, note, fill, line, dashed = false) {
  const band = slide.shapes.add({
    geometry: "roundRect",
    position: { left, top, width, height },
    fill,
    line: { style: dashed ? "dashed" : "solid", fill: line, width: 1.2 },
    borderRadius: "rounded-xl",
  });
  addText(slide, label, left + 18, top + 10, 220, 26, { fontSize: 20, bold: true, color: TEXT });
  if (note) addText(slide, note, left + 238, top + 14, width - 258, 20, { fontSize: 13, color: MUTED });
  return band;
}

async function main() {
  const deck = Presentation.create({ slideSize: { width: W, height: H } });
  const slide = deck.slides.add();
  slide.background.fill = BG;

  addText(slide, "供应链业务架构：让订单全程可追踪", 58, 28, 1080, 58, {
    fontSize: 50,
    bold: true,
    color: TEXT,
  });
  addText(slide, "把原先依赖群聊、Excel 和人工追单的协作，沉淀为统一数据与完整履约闭环", 60, 92, 930, 26, {
    fontSize: 20,
    color: MUTED,
  });
  addText(slide, "实线：已可用能力   虚线：规划建设能力", 970, 92, 250, 22, {
    fontSize: 13,
    color: PRIMARY,
    alignment: "right",
  });

  const external = addBand(slide, 52, 130, 1176, 76, "业务参与方与外部数据", "", "#EEF5FF", "#B8D2FF");
  const operator = addBox(slide, { left: 305, top: 145, width: 135, height: 46, fill: "#FFFFFF", title: "运营人员", subtitle: "创建与跟进订单" });
  const supplier = addBox(slide, { left: 458, top: 145, width: 135, height: 46, fill: "#FFFFFF", title: "供应商", subtitle: "报价、接单、发货" });
  const warehouse = addBox(slide, { left: 611, top: 145, width: 135, height: 46, fill: "#FFFFFF", title: "仓库人员", subtitle: "拣货、入仓、确认" });
  const wecom = addBox(slide, { left: 800, top: 145, width: 165, height: 46, fill: "#EAFBF4", line: "#9DE0C4", title: "企业微信", subtitle: "组织与登录", titleColor: "#147A52" });
  const jky = addBox(slide, { left: 983, top: 145, width: 180, height: 46, fill: "#FFF7E7", line: "#F2D18B", title: "吉客云", subtitle: "主体、店铺、商品、订单数据", titleColor: "#A76200" });

  addBand(slide, 52, 221, 1176, 74, "主数据底座", "", "#FFFFFF", BORDER);
  const baseNodes = [
    [260, "经营主体", "公司与付款主体"],
    [460, "销售渠道 / 店铺", "平台与店铺信息"],
    [695, "商品 SKU", "品牌、品类、规格"],
    [895, "供应商", "企业与成员资料"],
  ];
  baseNodes.forEach(([left, title, subtitle]) => addBox(slide, { left, top: 240, width: 175, height: 40, fill: "#F7FAFF", title, subtitle, line: "#D8E4F7" }));

  const flowBand = addBand(slide, 52, 310, 1176, 172, "供应链履约闭环", "", "#FFFFFF", "#9FC4FF");
  const flow = [
    [92, "入仓订单", "订单进入系统"],
    [270, "供应商协同", "定向推单"],
    [448, "报价 / 成交", "确认供货关系"],
    [626, "拣货入仓", "选择仓库、录入串码"],
    [804, "物流信息", "单号与状态维护"],
    [982, "订单完成", "结果可回溯"],
  ];
  const flowShapes = flow.map(([left, title, subtitle]) => addBox(slide, {
    left, top: 360, width: 145, height: 76, fill: "#F8FBFF", line: "#9FC4FF", title, subtitle,
  }));
  for (let index = 0; index < flowShapes.length - 1; index += 1) {
    slide.shapes.connect(flowShapes[index], flowShapes[index + 1], {
      kind: "straight",
      fromSide: "right",
      toSide: "left",
      line: { style: "solid", fill: PRIMARY, width: 2 },
      head: { type: "arrow", width: "sm", length: "sm" },
    });
  }

  addBand(slide, 52, 497, 1176, 75, "财务与经营管理", "", "#F8FFFB", "#9DE0C4");
  addBox(slide, { left: 310, top: 514, width: 200, height: 42, fill: "#FFFFFF", line: "#9DE0C4", title: "结算与付款", subtitle: "结算单、付款主体" });
  addBox(slide, { left: 540, top: 514, width: 200, height: 42, fill: "#FFFFFF", line: "#9DE0C4", title: "费用与资金参数", subtitle: "保证金、回款天数" });
  addBox(slide, { left: 770, top: 514, width: 200, height: 42, fill: "#FFFFFF", line: "#9DE0C4", title: "经营数据沉淀", subtitle: "订单、成本、费用基础" });

  addBand(slide, 52, 588, 1176, 84, "下一阶段：经营分析", "规划建设中 - 让管理层每天看清经营结果", "#F5F8FE", "#B8C5DC", true);
  const future = [
    [300, "每日发货同步"],
    [520, "成本费用核算"],
    [740, "经营统计看板"],
    [960, "利润分析"],
  ];
  future.forEach(([left, label]) => addText(slide, label, left, 626, 170, 26, {
    fontSize: 18, bold: true, color: PRIMARY, alignment: "center",
  }));
  addText(slide, "无界电商供应链经营底座", 60, 687, 360, 18, { fontSize: 12, color: MUTED });
  addText(slide, "目标：减少人工协同成本，让订单过程和经营结果都看得见", 650, 687, 570, 18, { fontSize: 12, color: MUTED, alignment: "right" });

  slide.speakerNotes.textFrame.setText([
    "[讲解稿｜约10分钟]",
    "1. 开场（1分钟）：以前一笔订单从采购到入仓，靠谁记、谁催、谁核对？现在系统把这些角色和动作放进同一条链路。",
    "2. 外部与人员（1分钟）：运营、供应商、仓库人员在系统内协同；企业微信负责组织和登录，吉客云提供店铺、商品、订单等基础业务数据。",
    "3. 主数据底座（2分钟）：主体、店铺、商品和供应商采用统一口径。后续订单、结算和经营分析不再各自维护一份表格。",
    "4. 履约闭环（4分钟）：从入仓订单开始，到供应商协同、报价成交、拣货入仓、物流信息、订单完成，每一环都能看到状态、责任人和处理记录。它解决的不是单一操作效率，而是降低漏单、错单和反复核对。",
    "5. 经营延展（1分钟）：订单完成后形成结算、付款和费用基础，为经营数据沉淀创造条件。",
    "6. 收尾（1分钟）：下一阶段将每日发货、成本费用和利润统一到经营看板中。系统将从人工协同工具，逐步成为供应链经营底座。",
    "[Sources] 内部项目现状：ADM 现有菜单、业务模块与集成配置；本页不引用外部统计数据。",
  ]);
  slide.speakerNotes.setVisible(true);

  await saveBlob(PREVIEW, await deck.export({ slide, format: "png", scale: 1 }));
  const pptx = await PresentationFile.exportPptx(deck);
  await pptx.save(OUT);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
