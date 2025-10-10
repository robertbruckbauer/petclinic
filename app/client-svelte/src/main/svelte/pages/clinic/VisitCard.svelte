<script>
  import Text from "../../components/Text";
  import TextArea from "../../components/TextArea";

  let { visit } = $props();

  let newVisitPetName = $state();
  let newVisitDate = $state();
  let newVisitText = $state();
  let newVisitVetName = $state();
  $effect(() => {
    newVisitPetName = visit.petItem?.text;
    newVisitDate = visit.date;
    newVisitText = visit.text;
    newVisitVetName = visit.vetItem?.text;
  });
</script>

<div class="flex flex-col border rounded-md p-2">
  <div class="flex flex-row gap-1">
    <div class="w-full">
      <Text value={newVisitPetName} label="Pet" />
    </div>
    <div class="w-48">
      <Text value={newVisitDate} label="Treatment" />
    </div>
  </div>
  <div class="full">
    <Text value={newVisitVetName} label="Vet" />
  </div>
  <div class="w-full">
    <TextArea value={newVisitText} disabled label="Diagnosis" />
  </div>
</div>
